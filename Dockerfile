FROM maven:3.8.8-eclipse-temurin-21 as builder
WORKDIR /workspace
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /workspace/target/book-catalog-service-*.jar app.jar
USER appuser
EXPOSE 8080
# Default to production profile (override in compose)
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]