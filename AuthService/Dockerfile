FROM openjdk:11-jdk-slim
RUN addgroup --system spring
RUN adduser --system --ingroup spring spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV SERVER_PORT=8080
ENTRYPOINT ["java","-jar","/app.jar", "--server.port=${SERVER_PORT}"]