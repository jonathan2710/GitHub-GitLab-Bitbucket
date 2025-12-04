FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace/app

# Copy sources and build jar
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=build /workspace/app/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
