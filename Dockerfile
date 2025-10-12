FROM eclipse-temurin:21 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk AS jlink
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base,java.logging,java.sql \
    --output /custom-jre \
    --strip-debug --no-header-files --no-man-pages

FROM debian:bookworm-slim
WORKDIR /app
COPY --from=build /app/target/jobsy-rest-api-1.0.jar jobsy.jar
COPY --from=jlink /custom-jre /opt/jre
ENV PATH="/opt/jre/bin:$PATH"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "jobsy.jar"]
