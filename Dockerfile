FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests package
FROM eclipse-temurin:17-jre
COPY --from=build /app/target/ecotrack-1.0.jar /app.jar
CMD ["java","-jar","/app.jar"]
