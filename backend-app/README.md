# Build instructions

```
$ ./gradlew bootJar
$ docker build -t kazusato/spring-jpa-example:0.0.1-SNAPSHOT --build-arg JAR_FILE=build/libs/spring-jpa-example-0.0.1-SNAPSHOT.jar .
$ docker run -d --name jpa -p 8080:8080 -e DB_URL=jdbc:postgresql://my-work-pg/myworkdb -e DB_USERNAME=mywork -e DB_PASSWORD=dummypassword --network my-work-network kazusato/spring-jpa-example:0.0.1-SNAPSHOT 
$ docker tag kazusato/spring-jpa-example:0.0.1-SNAPSHOT 999999999999.dkr.ecr.ap-northeast-1.amazonaws.com/kazusato/spring-jpa-example:0.0.1-SNAPSHOT
$ aws ecr get-login --no-include-email | sh -
$ docker push 999999999999.dkr.ecr.ap-northeast-1.amazonaws.com/kazusato/spring-jpa-example:0.0.1-SNAPSHOT
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
