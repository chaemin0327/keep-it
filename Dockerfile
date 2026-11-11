FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/
RUN chmod +x mvnw && ./mvnw -B -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
RUN mkdir -p /app/data && chown 10001:10001 /app/data
ENV SERVER_ADDRESS=0.0.0.0
USER 10001
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
