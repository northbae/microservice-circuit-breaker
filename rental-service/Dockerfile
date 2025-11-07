FROM maven:3.9.6-amazoncorretto-17 AS builder
WORKDIR /rental-service
COPY pom.xml .
COPY rental-service-api ./rental-service-api
COPY rental-service-db ./rental-service-db
COPY rental-service-impl ./rental-service-impl
RUN mvn clean package -DskipTests

FROM amazoncorretto:17
WORKDIR /rental-service
COPY --from=builder /rental-service/rental-service-impl/target/*.jar rental-service.jar
EXPOSE 8060
ENTRYPOINT ["java", "-jar", "rental-service.jar"]