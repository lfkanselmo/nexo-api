FROM gradle:9.7-jdk25-alpine AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S nexo && adduser -S nexo -G nexo
WORKDIR /app
COPY --from=build /app/build/libs/nexo-api-*.jar app.jar
USER nexo
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
