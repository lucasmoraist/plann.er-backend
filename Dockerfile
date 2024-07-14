FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/planner-0.0.1-SNAPSHOT.jar planner.jar

EXPOSE 8080

CMD ["java", "-jar", "planner.jar"]