FROM openjdk:8-jdk-alpine
ADD target/svc-spotten-s3bucket-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8589