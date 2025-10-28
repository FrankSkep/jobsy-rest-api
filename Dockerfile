# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copiar solo archivos de Maven primero para aprovechar cache
COPY pom.xml mvnw ./
COPY .mvn .mvn
# Descargar dependencias sin recompilar el código
RUN ./mvnw dependency:go-offline

# Copiar el resto del código
COPY src ./src

# Compilar sin tests
RUN ./mvnw package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar el JAR generado
COPY --from=builder /app/target/jobsy-rest-api-1.0.jar jobsy.jar

EXPOSE 8080

# Optimización JVM para arranque más rápido y menor memoria
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-XX:+UseCompressedOops", "-jar", "jobsy.jar"]
