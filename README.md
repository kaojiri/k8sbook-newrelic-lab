# k8sbook-repo with New Relic!!!

# 前提

* amazoncorretto11で動作確認しています。build時はamazoncorretto11を使うようにしてください。
* 書籍のアプリケーションをベースにしていますが、frontend側には触れていません。直接ELBへアクセスするものになっています

# 環境構築

## ベースリソース作成

CloudFormationで。stack名は`eks-work-base`で。

## クラスター作成

```bash
# サブネットIDはベースリソースで作成されたものを指定する
# キーペアは事前に作成しておく
eksctl create cluster \
 --vpc-public-subnets subnet-0d2ded9127b4ffd77,subnet-0e3407551a6881709,subnet-01997ced1df64764d \
 --name eks-work-cluster \
 --region ap-northeast-1 \
 --version 1.14 \
 --nodegroup-name eks-work-nodegroup \
 --node-type t2.small \
 --nodes 2 \
 --nodes-min 0 \
 --nodes-max 5 \
 --ssh-public-key default-keypair
```

## DB作成

CloudFormationで。stack名は`eks-work-rds`で。  
routeTablesはBaseスタックのoutput参照。

## DBセットアップ

Session ManagerでOpeサーバに接続して実施
```bash
sudo yum install -y git
sudo amazon-linux-extras install -y postgresql10
cd
git clone https://github.com/kazusato/k8sbook.git
```

`DBユーザ名とパスワードはSecret Managerで確認する`

```bash
# CloudFormationで作成されたものを設定する
DB_HOSTNAME="eks-work-db.XXXXXXXXXXX.ap-northeast-1.rds.amazonaws.com"

# User / DB作成
createuser -d -U eksdbadmin -P -h ${DB_HOSTNAME} mywork
# 最初の2回はアプリケーション用データベースユーザーのパスワード
# 最後の1回はデータベース管理者パスワード

createdb -U mywork -h ${DB_HOSTNAME} -E UTF8 myworkdb
# myworkユーザーのパスワード

# データ登録
psql -U mywork -h ${DB_HOSTNAME} myworkdb
# myworkユーザーのパスワード

\i k8sbook/backend-app/scripts/10_ddl.sql
\i k8sbook/backend-app/scripts/20_insert_sample_data.sql

\q
```

## kubectl セットアップ

```bash
# sample用の名前空間を作成する
kubectl apply -f 20_create_namespace_k8s.yaml

# eks-workの名前空間をdefaultとするコンテキストを作成する
kubectl config set-context eks-work --cluster eks-work-cluster.ap-northeast-1.eksctl.io \
 --user kaizawa@eks-work-cluster.ap-northeast-1.eksctl.io \
 --namespace eks-work

# eks-workコンテキストを使う
kubectl config use-context eks-work
```

## backend-app, extrenal-app

### DB接続情報登録

```bash
# DB接続文字列をsecretとして登録
DB_URL=jdbc:postgresql://eks-work-db.XXXXXXXXXXX.ap-northeast-1.rds.amazonaws.com/myworkdb \
DB_PASSWORD='uZAcej~xaFsZL2Zs' \
envsubst < 21_db_config_k8s.yaml.template | \
kubectl apply -f -
```

### New Relic設定

gradleで以下を実行する。

```bash
./gradlew downloadNewrelic unzipNewrelic
```

backend-app配下にnewrelicフォルダが作成されるので、newrelic/newrelic.ymlを以下のように修正する

* `license_key:` を設定
* `app_name:`を設定

```yaml
app_name: k8sbook-backend-app # backend-appの場合
app_name: k8sbook-external-app # external-appの場合
```

### ビルド

```bash
# Javaへのパスを通す
export JAVA_HOME=$(/usr/libexec/java_home -v 11)

# backend-appビルド
./gradlew clean build
docker build -t k8sbook/backend-app:1.0.0 --build-arg JAR_FILE=build/libs/backend-app-0.3.0.jar .
docker tag k8sbook/backend-app:1.0.0 123456789012.dkr.ecr.ap-northeast-1.amazonaws.com/k8sbook/backend-app:1.0.0
docker push 123456789012.dkr.ecr.ap-northeast-1.amazonaws.com/k8sbook/backend-app:1.0.0

# external-appビルド
./gradlew clean build
docker build -t k8sbook/external-app:1.0.0 --build-arg JAR_FILE=build/libs/backend-app-0.3.0.jar .
docker tag k8sbook/external-app:1.0.0 123456789012.dkr.ecr.ap-northeast-1.amazonaws.com/k8sbook/external-app:1.0.0
docker push 123456789012.dkr.ecr.ap-northeast-1.amazonaws.com/k8sbook/external-app:1.0.0
```

### デプロイ

```bash
# backend-app デプロイ
ECR_HOST=123456789012.dkr.ecr.ap-northeast-1.amazonaws.com \
envsubst < 22_deployment_backend-app_k8s.yaml.template | \
kubectl apply -f -
# external-app デプロイ
ECR_HOST=123456789012.dkr.ecr.ap-northeast-1.amazonaws.com \
envsubst < 22_deployment_external-app.yaml | \
kubectl apply -f -
```

`READYになるまで2−3分かかります`

### Service(ELB)作成

```bash
kubectl apply -f 23_service_backend-app_k8s.yaml
kubectl apply -f 23_service_external-app-service.yaml
```

`http://<backend-appのELB>:8080/`にアクセスすると、Top画面が表示されるはずです。

## batch-app

### バケット作成

CloudFormationで。スタック名は`eks-work-batch`  
バケット名重複を避けたいので、サフィックスをパラメーターに指定する。

### New Relic設定

gradleで以下を実行する。

```bash
./gradlew downloadNewrelic unzipNewrelic
```

backend-app配下にnewrelicフォルダが作成されるので、newrelic/newrelic.ymlを以下のように修正する

* `license_key:` を設定
* `app_name:`を設定

```yaml
app_name: k8sbook-batch-app
```

### ビルド

```bash
# batch-appのs3接続先設定をconfigMapとして登録
# 環境変数はCloudFormationで指定したものを指定する
BUCKET_SUFFIX=kaizawa \
envsubst < 41_config_map_batch_k8s.yaml.template | kubectl apply -f -

# batch-appのAWSCLI認証設定をsecretとして登録
# CloudFormationで作成されたSSMパラメーターから指定する
AWS_ACCESSKEY=XXXXXXXXXXXXXXXX \
AWS_SECRETKEY=XXXXXXXXXXXXXXXX \
envsubst < 42_batch_secrets_k8s.yaml.template | kubectl apply -f -

# sample-dataをup
BUCKET_SUFFIX=kaizawa
aws s3 sync ../batch-app/sample_data/normal s3://eks-work-batch-${BUCKET_SUFFIX}/locationData --delete --include "*" --acl public-read

# build
./gradlew clean build
docker build -t k8sbook/batch-app:1.0.0 --build-arg JAR_FILE=build/libs/batch-app-0.2.1.jar .
docker tag k8sbook/batch-app:1.0.0 123456789012.dkr.ecr.ap-northeast-1.amazonaws.com/k8sbook/batch-app:1.0.0
docker push 123456789012.dkr.ecr.ap-northeast-1.amazonaws.com/k8sbook/batch-app:1.0.0


# batch-appデプロイ(eks-workディレクトリで)
ECR_HOST=123456789012.dkr.ecr.ap-northeast-1.amazonaws.com \
envsubst < 43_cronjob_k8s.yaml.template | \
kubectl apply -f -
```