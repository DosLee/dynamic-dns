FROM openjdk:8-jdk-alpine
VOLUME /log/
RUN addgroup -S the2333 && adduser -S the2333 -G the2333
RUN chown -R the2333:the2333 /log
USER the2333:the2333
COPY ./*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]