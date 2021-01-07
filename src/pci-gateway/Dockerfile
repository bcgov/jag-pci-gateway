#############################################################################################
###              Stage where Docker is building spring boot app using maven               ###
#############################################################################################
FROM maven:3.6.3-jdk-8 as build

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /build/src/
RUN mvn package

#############################################################################################

#############################################################################################
### Stage where Docker is running a java process to run a service built in previous stage ###
#############################################################################################
FROM openjdk:8-jdk-slim

COPY --from=build ./build/target/pci-gateway-*.jar /app/service.jar

CMD ["java", "-jar", "/app/service.jar"]
#############################################################################################
