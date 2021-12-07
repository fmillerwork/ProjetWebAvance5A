FROM openjdk:11-jdk-slim
RUN addgroup --system spring
RUN adduser --system --ingroup spring spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
#COPY production.properties /
ENV SERVER_PORT=8082
ENV AUTH_URL=http://localhost:8080
ENV PROFILE_URL=http://localhost:8081
ENTRYPOINT ["java","-jar","/app.jar", "--server.port=${SERVER_PORT}", "--service.authentication=${AUTH_URL}", "--service.profiles=${PROFILE_URL}"]
