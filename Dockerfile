FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
COPY data ./data
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]