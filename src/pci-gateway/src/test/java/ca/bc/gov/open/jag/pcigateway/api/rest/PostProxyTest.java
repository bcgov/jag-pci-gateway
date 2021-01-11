package ca.bc.gov.open.jag.pcigateway.api.rest;

import ca.bc.gov.open.jag.pcigateway.api.RestProxyController;
import ca.bc.gov.open.jag.pcigateway.config.AppProperties;
import ca.bc.gov.open.jag.pcigateway.config.GatewayRestClientProperties;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostProxyTest {
    private static final String MERCHANT_ID = "merchantId";
    private static final String API_URL = "http://localhost:8080";
    public static final String REQUEST_URI = "/pcigw/payments";
    public static final String PASSCODE_FOUND = "Passcode bWVyY2hhbnRJZDpC";
    public static final String PASSCODE_NOT_FOUND = "Passcode bWVyY2hhbnRJZDpD";
    public static final String BODY_200 = "{test:200}";
    public static final String BODY_400 = "{test:400}";

    @Mock
    private RestTemplate restTemplateMock;

    private RestProxyController sut;

    @BeforeAll
    public void beforeAll() {

        MockitoAnnotations.openMocks(this);

        AppProperties appProperties = new AppProperties();

        appProperties.setApiUrl(API_URL);
        List<GatewayRestClientProperties> restClients = new ArrayList<>();
        GatewayRestClientProperties testClient = new GatewayRestClientProperties();
        testClient.setMerchantId(MERCHANT_ID);
        testClient.setApiKey("A");
        testClient.setGatewayApiKey("B");
        restClients.add(testClient);
        appProperties.setGatewayRestClients(restClients);

        sut = new RestProxyController(appProperties, restTemplateMock);

    }

    @Test
    @DisplayName("200: call to bambora succeeded")
    public void withValidApiKeyCallBambora() {

        Mockito.when(restTemplateMock.postForEntity(ArgumentMatchers.isA(String.class), any(), any())).thenReturn(ResponseEntity.ok(""));

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI(REQUEST_URI);

        ResponseEntity<String> result = sut.postProxy(mockHttpServletRequest, PASSCODE_FOUND, BODY_200);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());

    }

    @Test
    @DisplayName("400: call to bambora failed")
    public void withValidApiKeyBamboraCallFails() {

        Mockito.when(restTemplateMock.postForEntity(ArgumentMatchers.isA(String.class), any(), any())).thenReturn(ResponseEntity.badRequest().body(""));

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI(REQUEST_URI);

        ResponseEntity<String> result = sut.postProxy(mockHttpServletRequest, PASSCODE_FOUND, BODY_400);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());

    }


    @Test
    @DisplayName("404: api key not found return 404")
    public void withInValidApiKeyBamboraCallFails() {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI(REQUEST_URI);

        ResponseEntity<String> result = sut.postProxy(mockHttpServletRequest, PASSCODE_NOT_FOUND, "{test:test}");

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

    }
}
