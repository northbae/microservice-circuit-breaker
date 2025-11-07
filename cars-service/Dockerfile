FROM maven:3.9.6-amazoncorretto-17 AS builder
WORKDIR /cars-service
COPY pom.xml .
COPY cars-service-api ./cars-service-api
COPY cars-service-db ./cars-service-db
COPY cars-service-impl ./cars-service-impl
RUN mvn clean package -DskipTests

FROM amazoncorretto:17
WORKDIR /cars-service
COPY --from=builder /cars-service/cars-service-impl/target/*.jar cars-service.jar
EXPOSE 8070
ENTRYPOINT ["java", "-jar", "cars-service.jar"]