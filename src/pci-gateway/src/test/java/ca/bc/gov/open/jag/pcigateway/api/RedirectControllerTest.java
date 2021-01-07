package ca.bc.gov.open.jag.pcigateway.api;

import ca.bc.gov.open.jag.pcigateway.config.AppProperties;
import ca.bc.gov.open.jag.pcigateway.config.GatewayClientProperty;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedirectControllerTest {

    private static final String MERCHANT_ID = "merchantId";
    private static final String REDIRECT_URL = "http://localhost:8080";

    private RedirectController sut;

    @Mock
    private RestTemplate restTemplateMock;

    @BeforeAll
    public void beforeAll() {

        MockitoAnnotations.openMocks(this);

        Mockito.when(restTemplateMock.getForEntity(Mockito.any(URI.class), Mockito.eq(String.class)))
                .thenReturn(ResponseEntity.ok().build());

        AppProperties appProperties = new AppProperties();
        appProperties.setRedirectUrl(REDIRECT_URL);
        List<GatewayClientProperty> clients = new ArrayList<>();
        GatewayClientProperty testClient = new GatewayClientProperty();
        testClient.setMerchantId(MERCHANT_ID);
        testClient.setHashKey("1234");
        testClient.setGatewayHashKey("5678");
        clients.add(testClient);
        appProperties.setGatewayClients(clients);
        sut = new RedirectController(appProperties, restTemplateMock);

    }

    @Test
    @DisplayName("200: with valid parameters should rebuild url")
    public void withValidParamsShouldReturnValidUrl() throws MissingServletRequestParameterException {

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");

        RedirectView result = sut.paymentRedirect(mockHttpServletRequest);

        Assertions.assertEquals("http://localhost:8080/scripts/Payment/Payment.asp?merchant_id=merchantId&hashValue=E2EEA71D02D92AD968A9A63A44862413", result.getUrl());

    }

    @Test
    @DisplayName("200: with valid parameters and additional should rebuild url")
    public void withValidParamsAndMoreShouldReturnValidUrl() throws MissingServletRequestParameterException {

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");
        mockHttpServletRequest.setParameter("otherparams", "otherparams");

        RedirectView result = sut.paymentRedirect(mockHttpServletRequest);

        Assertions.assertEquals("http://localhost:8080/scripts/Payment/Payment.asp?merchant_id=merchantId&hashValue=E2EEA71D02D92AD968A9A63A44862413&otherparams=otherparams", result.getUrl());

    }

    @Test
    @DisplayName("200: test variation of merchant it")
    public void testVariationOfMerchantId() throws MissingServletRequestParameterException {


        String[] merchantIdKeys = new String[] {
                "merchantid",
                "Merchantid",
                "merchantId",
                "MerchantId",
                "merchant_id",
                "Merchant_id",
                "merchant_Id",
                "Merchant_Id" };

        for (String key :
                merchantIdKeys) {

            MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
            mockHttpServletRequest.setParameter(key, MERCHANT_ID);
            mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");
            RedirectView actual = sut.paymentRedirect(mockHttpServletRequest);
            String expected = MessageFormat.format(
                    "http://localhost:8080/scripts/Payment/Payment.asp?{0}=merchantId&hashValue=E2EEA71D02D92AD968A9A63A44862413"
                    , key);
            Assertions.assertEquals(expected, actual.getUrl());

        }

    }

    @Test
    @DisplayName("400: without hash key should return bad request")
    public void withoutHashKeyShouldReturn400() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.paymentRedirect(mockHttpServletRequest));
    }

    @Test
    @DisplayName("400: with blank merchant id should return bad request")
    public void withoutMerchantIdValueShouldReturnBadRequest() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", "  ");
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.paymentRedirect(mockHttpServletRequest));
    }

    @Test
    @DisplayName("400: without merchant id should return bad request")
    public void withMerchantIdShouldReturnBadRequest() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.paymentRedirect(mockHttpServletRequest));
    }

    @Test
    @DisplayName("400: with invalid hash should return bad request")
    public void withInvalidHashShouldReturnBadRequest() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        mockHttpServletRequest.setParameter("hashValue", "ACDC");
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.paymentRedirect(mockHttpServletRequest));
    }

    @Test
    @DisplayName("400: with unknown merchant id should return bad request")
    public void withInvalidMerchantIdReturnBadRequest() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", "GNR");
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.paymentRedirect(mockHttpServletRequest));
    }


    @Test
    @DisplayName("200: Transaction status check should execute request")
    public void withValidParamsShouldExecuteHttpCallToRemoteService() throws MissingServletRequestParameterException {


        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");
        mockHttpServletRequest.setParameter("otherparams", "otherparams");

        ResponseEntity<String> actual = sut.statusRedirect(mockHttpServletRequest);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());


    }



}
