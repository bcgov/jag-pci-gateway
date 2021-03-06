# jag-pci-gateway

[![Maintainability](https://api.codeclimate.com/v1/badges/89aaf5706ffd314a222c/maintainability)](https://codeclimate.com/github/bcgov/jag-pci-gateway/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/89aaf5706ffd314a222c/test_coverage)](https://codeclimate.com/github/bcgov/jag-pci-gateway/test_coverage) ![Integration Tests](https://github.com/bcgov/jag-pci-gateway/workflows/Integration%20Tests/badge.svg)

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

## Logging

### Log files

Configure the following in the application yml

```yml
logging:
 path: ./logs
```

### Splunk

Package the application with the splunk profile enabled

```bash
mvn clean package -f src/pci-gateway/pom.xml -P splunk
```

Configure the following in the application yml

```yml
splunk:
 url: http://localhost:8080
 token: token
 source: pci-gateway
```

## Encryted Parameters

Download the [jasypt-1.9.3 binary](https://github.com/jasypt/jasypt/releases/tag/jasypt-1.9.3)

To encrypt parameters run:

```bash
java -cp <yourjasyptlocation>/jasypt-1.9.3/lib/jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="somevalue" password=somepassword algorithm=PBEWithMD5AndDES
```

Add secret in run command <yoursecret> and in application.yml wrap the encrypted value: ENC(xyz123==)

ex:

```yaml
myproperty: ENC(your-encripted-string)
```
