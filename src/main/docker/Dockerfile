FROM openjdk:8-jdk-alpine
VOLUME /log/
RUN chmod -R 777 /log/
RUN addgroup -S the2333 && adduser -S the2333 -G the2333
USER the2333:the2333
COPY ./*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]