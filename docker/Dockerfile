FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/secure-statements.jar app.jar
RUN mkdir -p /app/secure-storage
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]