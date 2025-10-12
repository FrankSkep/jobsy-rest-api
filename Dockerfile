# Etapa de build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .

# Empaqueta la app sin correr tests
RUN ./mvnw clean package -DskipTests

# Etapa final (runtime)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia el JAR construido
COPY --from=build /app/target/jobsy-rest-api-1.0.jar jobsy.jar

# Expone el puerto que Render usará
EXPOSE 8080

# Flags de JVM para cold start más rápido y bajo consumo
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-XX:+UseCompressedOops", "-jar", "jobsy.jar"]
