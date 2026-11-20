FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/platform-__NAME__.jar app.jar
EXPOSE __PORT__
ENTRYPOINT ["java", "-jar", "app.jar"]
