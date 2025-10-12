FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/jobsy-rest-api-1.0.jar jobsy.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-XX:+UseCompressedOops", "-jar", "jobsy.jar"]
