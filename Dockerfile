# BUILD STAGE

FROM maven:3.8-openjdk-17-slim AS build

RUN mkdir /project
COPY . /project
WORKDIR /project
RUN mvn clean package -DskipTests

# RUN STAGE

FROM eclipse-temurin:17-jdk-alpine

RUN mkdir /app
COPY --from=build /project/target/*.jar /app/crypto.jar
WORKDIR /app
CMD "java" "-jar" "crypto.jar"
