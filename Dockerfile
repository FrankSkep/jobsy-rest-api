FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/jobsy-rest-api-1.0.jar jobsy.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "jobsy.jar"]