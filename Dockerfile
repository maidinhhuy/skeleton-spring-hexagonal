FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY modules/infrastructure/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
