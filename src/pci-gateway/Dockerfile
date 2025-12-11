#############################################################################################
###              Stage where Docker is building spring boot app using maven               ###
#############################################################################################
FROM maven:3.9.9-eclipse-temurin-21-jammy as build

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /build/src/
RUN mvn package -DskipTests

#############################################################################################

#############################################################################################
### Stage where Docker is running a java process to run a service built in previous stage ###
#############################################################################################
FROM eclipse-temurin:21-jre-jammy

COPY --from=build ./build/target/pci-gateway-*.jar /app/service.jar

CMD ["java", "-jar", "/app/service.jar"]
#############################################################################################
