# Build instructions

```
$ ./gradlew bootJar
$ docker build -t kazusato/batch-app:0.1.0 --build-arg JAR_FILE=build/libs/batch-app-0.1.0.jar .
$ docker run --name batch -e DB_URL=jdbc:postgresql://my-work-pg/myworkdb -e DB_USERNAME=mywork -e DB_PASSWORD=dummypassword -e CLOUD_AWS_CREDENTIALS_ACCESSKEY=xxxx -e CLOUD_AWS_CREDENTIALS_SECRETKEY=xxxx -e CLOUD_AWS_REGION_STATIC=ap-northeast-1 -e SAMPLE_APP_BATCH_BUCKET_NAME=eks-work-batch -e SAMPLE_APP_BATCH_FOLDER_NAME=locationData -e SAMPLE_APP_BATCH_RUN=true --network my-work-network kazusato/batch-app:0.1.0
$ docker tag kazusato/spring-jpa-example:0.0.1-SNAPSHOT 999999999999.dkr.ecr.ap-northeast-1.amazonaws.com/kazusato/spring-jpa-example:0.0.1-SNAPSHOT
$ aws ecr get-login --no-include-email | sh -
$ docker push 999999999999.dkr.ecr.ap-northeast-1.amazonaws.com/kazusato/spring-jpa-example:0.1.0
```

# DBが必要なテストを実行する

```
$ ./gradlew testWithDatabase

```

# ECR操作

```
$ aws ecr describe-repositories 
$ aws ecr describe-repositories --repository-names kazusato/spring-jpa-example
$ aws ecr describe-images  --repository-name kazusato/spring-jpa-example
$ aws ecr batch-delete-image --repository-name kazusato/spring-jpa-example --image-ids imageDigest=sha256:990ba0c6c23f9340b650704df217a13f8a9fbbb11bea5672df05c213792759ab
$ aws ecr batch-delete-image --repository-name kazusato/spring-jpa-example --image-ids imageTag=0.0.1-SNAPSHOT
```

# Related pages
