package gov.bc.ca.open.jag.pciGatewayTestRunner.stepDefinitions;

import gov.bc.ca.open.jag.pciGatewayTestRunner.TestConfig;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@CucumberContextConfiguration
@SpringBootTest(classes = TestConfig.class)
public class ApiCallSD {

    private String actualQueryParams;
    private String actualRequest;
    private Response actualResponse;
    private String actualHash;

    @Value("${PCI_GATEWAY_URL:http://localhost:8080}")
    private String pciGatewayBaseUrl;

    @Value("${PCI_GATEWAY_KEY:RESTASSURED}")
    private String pciGatewayKey;

    @Given("An api request to be proxied to bambora with path {string} and with queryParams: {string}")
    public void anApiRequestToBeProxiedToBambora(String path, String queryParams) {
        actualQueryParams = queryParams;
        actualRequest = MessageFormat.format("{0}{1}", pciGatewayBaseUrl, path);
    }

    @When("Calling PCI Gateway to proxy the request")
    public void callingPCIGatewayToProxyTheRequest() {

        RequestSpecification request = RestAssured
                .given()
                .contentType(ContentType.JSON);

        actualHash = buildStringParams();

        actualResponse = request
                .when()
                .get(MessageFormat.format("{0}?{1}", actualRequest, actualHash))
                .then()
                .extract()
                .response();

    }

    @Then("The response should be {int}")
    public void theResponseShouldBeReturnedBack(int httpStatusCode) {

        Assert.assertEquals(httpStatusCode, actualResponse.getStatusCode());
        JsonPath jsonPath = JsonPath.from(actualResponse.asString());

        String expectedUrlParams = jsonPath.getString("url").split("\\?")[1];
        Assert.assertEquals(actualQueryParams, expectedUrlParams.substring(0, expectedUrlParams.indexOf("&hashValue")));
        Assert.assertTrue(actualHash != jsonPath.getString("args.hashValue"));

    }

    private String buildStringParams() {

        return MessageFormat.format("{0}&{1}={2}&{3}={4}",  actualQueryParams, "hashValue", getHash(actualQueryParams), "hashExpiry", getExpiry());

    }

    private String getExpiry() {

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddkkmm");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE,  5);
        return sdfDate.format(cal.getTime());

    }

    private String getHash(String message) {

        String digest = DigestUtils.md5Hex(MessageFormat.format("{0}{1}", message, pciGatewayKey));
        return digest.toUpperCase();

    }



}
