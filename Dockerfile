FROM openjdk:21-jdk

ENV SPRING_PROFILES_ACTIVE=prod

WORKDIR /app

COPY "target/invoice-app-dev-0.0.1-SNAPSHOT.jar" .

CMD ["java", "-jar", "invoice-app-dev-0.0.1-SNAPSHOT.jar"]
