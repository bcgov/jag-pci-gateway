# jag-pci-gateway

[![Maintainability](https://api.codeclimate.com/v1/badges/89aaf5706ffd314a222c/maintainability)](https://codeclimate.com/github/bcgov/jag-pci-gateway/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/89aaf5706ffd314a222c/test_coverage)](https://codeclimate.com/github/bcgov/jag-pci-gateway/test_coverage)

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

## Ecryted Parameters

To encrypt parameters run:

```bash
java -cp <yourjasyptlocation>/jasypt-1.9.3/lib/jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="somevalue" password=somepassword algorithm=PBEWithMD5AndDES
```

Add secret in run command <yoursecret> and in application.yml wrap the encrypted value: ENC(xyz123==)
