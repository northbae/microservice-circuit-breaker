FROM maven:3.9.6-amazoncorretto-17 AS builder
WORKDIR /payment-service
COPY pom.xml .
COPY payment-service-api ./payment-service-api
COPY payment-service-db ./payment-service-db
COPY payment-service-impl ./payment-service-impl
RUN mvn clean package -DskipTests

FROM amazoncorretto:17
WORKDIR /payment-service
COPY --from=builder /payment-service/payment-service-impl/target/*.jar payment-service.jar
EXPOSE 8050
ENTRYPOINT ["java", "-jar", "payment-service.jar"]