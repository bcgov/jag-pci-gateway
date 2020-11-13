# jag-pci-gateway

## Build

run

```bash
mvn clean install -f src/pci-gateway/pom.xml
```

## Package application

```bash
mvn clean package -f src/pci-gateway/pom.xml
```

## Run

```bash
java -Djasypt.encryptor.password=<yoursecret> -jar pci-gateway-0.0.1-SNAPSHOT.jar --spring.config.location=file:application.yml
```
