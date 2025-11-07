FROM maven:3.9.6-amazoncorretto-17 AS builder
WORKDIR /gateway-service
COPY gateway-service/pom.xml .
COPY gateway-service/gateway-service-api ./gateway-service-api
COPY gateway-service/gateway-service-impl ./gateway-service-impl

COPY cars-service/pom.xml ./cars-service/
COPY cars-service/cars-service-api ./cars-service/cars-service-api
COPY cars-service/cars-service-impl ./cars-service/cars-service-impl
COPY cars-service/cars-service-db ./cars-service/cars-service-db

COPY rental-service/pom.xml ./rental-service/
COPY rental-service/rental-service-api ./rental-service/rental-service-api
COPY rental-service/rental-service-impl ./rental-service/rental-service-impl
COPY rental-service/rental-service-db ./rental-service/rental-service-db

COPY payment-service/pom.xml ./payment-service/
COPY payment-service/payment-service-api ./payment-service/payment-service-api
COPY payment-service/payment-service-impl ./payment-service/payment-service-impl
COPY payment-service/payment-service-db ./payment-service/payment-service-db

RUN mvn -f cars-service/pom.xml clean install -DskipTests
RUN mvn -f payment-service/pom.xml clean install -DskipTests
RUN mvn -f rental-service/pom.xml clean install -DskipTests

RUN mvn clean package -DskipTests

FROM amazoncorretto:17
WORKDIR /gateway-service
COPY --from=builder /gateway-service/gateway-service-impl/target/*.jar gateway-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "gateway-service.jar"]