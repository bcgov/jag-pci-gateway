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

## REST proxy API

The gateway proxies requests under `/pcigw/v1/` to the configured payment gateway API. All routes require an `Authorization` header in this format:

```text
Authorization: Passcode <base64(merchantId:gatewayApiKey)>
```

The gateway validates the merchant ID and gateway API key, then forwards the request using the configured upstream API key.

| Method | Route | Request |
| --- | --- | --- |
| `POST` | `/pcigw/v1/{resource}` | JSON request body; `{resource}` is one path segment. |
| `GET` | `/pcigw/v1/profiles/{profileId}` | No request body. |
| `PUT` | `/pcigw/v1/profiles/{profileId}` | JSON request body. |
| `DELETE` | `/pcigw/v1/profiles/{profileId}` | No request body. |

For `POST` and `PUT`, the gateway forwards the request body as JSON. It returns the upstream response status, headers, and body; upstream HTTP error responses are relayed with their status and body. Payload schemas are defined by the upstream API, not by this gateway. Invalid or unrecognized credentials return `401 Unauthorized`.

### Consuming the REST API

You need a merchant ID and gateway API key from the gateway operator (the `merchant-id` and `gateway-api-key` values under `pci-gateway.gateway-rest-clients`). The real upstream API key is never exposed to consumers.

Build the `Authorization` header by Base64-encoding `merchantId:gatewayApiKey`. For example, merchant `A` with gateway key `B`:

```bash
echo -n "A:B" | base64   # QTpC
```

The decoded value must split into exactly two parts on `:`, so the merchant ID and key must not contain `:`.

```bash
# Create a payment (or any other resource)
curl -X POST https://<gateway-host>/pcigw/v1/payments \
  -H "Authorization: Passcode QTpC" \
  -H "Content-Type: application/json" \
  -d '{"amount": 10.00, "payment_method": "card"}'

# Read, update and delete a profile
curl https://<gateway-host>/pcigw/v1/profiles/123 -H "Authorization: Passcode QTpC"

curl -X PUT https://<gateway-host>/pcigw/v1/profiles/123 \
  -H "Authorization: Passcode QTpC" \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Doe"}'

curl -X DELETE https://<gateway-host>/pcigw/v1/profiles/123 -H "Authorization: Passcode QTpC"
```

Responses:

- Success: the upstream status and body are returned unchanged.
- Upstream 4xx/5xx: relayed with the original status and body.
- Unknown merchant ID or key, or a malformed passcode: `401` with an empty body.
- Missing `Authorization` header: `400`.

The REST routes do not use `merchant_id` or `hashValue` query parameters; those apply only to the redirect routes.

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
