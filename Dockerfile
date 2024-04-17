FROM eclipse-temurin:21-ubi9-minimal
VOLUME /temp
ARG JAR_FILE=build/libs/bookingbee-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/key.json src/main/resources/key.json
ENTRYPOINT ["java", "-jar", "/app.jar"]
