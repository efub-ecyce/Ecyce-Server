FROM arm64v8/eclipse-temurin:17-jdk-focal

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

COPY docker-compose.yml /app/docker-compose.yml

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/app.jar"]
