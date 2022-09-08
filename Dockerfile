FROM maven:3.6.3-openjdk-17-slim AS MAVEN_BUILD
COPY . /
RUN mvn clean package -Dmaven.test.skip

FROM openjdk:17.0.2-slim-buster
EXPOSE 8080
COPY --from=MAVEN_BUILD target/JWT_BASED_APP-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]