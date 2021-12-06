FROM openjdk:11-jdk-slim
RUN addgroup --system spring
RUN adduser --system --ingroup spring spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
#COPY production.properties /
ENV SERVER_PORT=8081
ENV PROFILE_URL=http://localhost:9090
ENV AUTH_URL=http://localhost:8080
ENTRYPOINT ["java","-jar","/app.jar", "--server.port=${SERVER_PORT}", "--service.authentification=${AUTH_URL}", "--service.profiles=${PROFILE_URL}"]