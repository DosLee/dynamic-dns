FROM openjdk:8-jdk-alpine
RUN addgroup -S the2333 && adduser -S the2333 -G the2333
USER the2333:the2333
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]