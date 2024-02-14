package ca.bc.gov.open.jag.pcigateway.api.legacy;

import ca.bc.gov.open.jag.pcigateway.api.LegacyRedirectController;
import ca.bc.gov.open.jag.pcigateway.api.RedirectController;
import ca.bc.gov.open.jag.pcigateway.config.AppProperties;
import ca.bc.gov.open.jag.pcigateway.config.GatewayClientProperty;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentRedirectTest {
    private static final String MERCHANT_ID = "merchantId";
    private static final String REDIRECT_URL = "http://localhost:8080";
    public static final String REQUEST_URI = "/pcigw/Payment/Payment.asp";

    private LegacyRedirectController sut;

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
        sut = new LegacyRedirectController(appProperties, restTemplateMock);

    }

    @Test
    @DisplayName("200: with valid parameters should rebuild url")
    public void withValidParamsShouldReturnValidUrl() throws MissingServletRequestParameterException {

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI(REQUEST_URI);
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        mockHttpServletRequest.setParameter("hashValue", "C059DCA04117EB205AA32521D46AF4D0");
        mockHttpServletRequest.setQueryString("merchant_id=merchantId&hashValue=C059DCA04117EB205AA32521D46AF4D0");
        RedirectView result = sut.requestRedirect(mockHttpServletRequest);

        Assertions.assertEquals("http://localhost:8080/scripts/Payment/Payment.asp?merchant_id=merchantId&hashValue=0F69EAC97FBB07CB1537A5EDB2DA8A0F", result.getUrl());

    }

    @Test
    @DisplayName("200: with valid parameters and additional should rebuild url")
    public void withValidParamsAndMoreShouldReturnValidUrl() throws MissingServletRequestParameterException {

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI(REQUEST_URI);
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        mockHttpServletRequest.setParameter("hashValue", "C059DCA04117EB205AA32521D46AF4D0");
        mockHttpServletRequest.setParameter("otherparams", "otherparams");
        mockHttpServletRequest.setQueryString(MessageFormat.format("merchant_id=merchantId&hashValue={0}&otherparams=otherparams", "C059DCA04117EB205AA32521D46AF4D0"));
        RedirectView result = sut.requestRedirect(mockHttpServletRequest);

        Assertions.assertEquals("http://localhost:8080/scripts/Payment/Payment.asp?merchant_id=merchantId&otherparams=otherparams&hashValue=0F69EAC97FBB07CB1537A5EDB2DA8A0F", result.getUrl());
    }

    @Test
    @DisplayName("200: test variation of merchant it")
    public void testVariationOfMerchantId() throws MissingServletRequestParameterException {


        final int MERCHANTID = 0;
        final int gwHASHVALUE = 1;  // USING gateway-hash-key TO COMPUTE HASHVALUE
        final int HASHVALUE = 2;    // USING hash-key TO COMPUTE HASHVALUE

        List<List<String>> queryParams = Arrays.asList(
                Arrays.asList("merchantid", "991AC2E3AD74C10388787E58DC226F9A", "E4A7D14747F7B63A61C63CF2B20CDC9D"),
                Arrays.asList("Merchantid", "D196E82AB7F54F727B77FA3991000024", "BD804E9A441DBAD657679A003B68EEA9"),
                Arrays.asList("merchantId", "921A55A22915C97065F325812C7FD6B1", "AD510A412C712504BF21B1986C4D6987"),
                Arrays.asList("MerchantId", "50F37DAF6B3347554F0064A8478206CE", "2378521011217A9421AD101D69B14890"),
                Arrays.asList("merchant_id", "C059DCA04117EB205AA32521D46AF4D0", "0F69EAC97FBB07CB1537A5EDB2DA8A0F"),
                Arrays.asList("Merchant_id", "40E4D58C423982D2DB1AAB0CC984686B", "70AFC4C0A3525CA8CDB1024F35731229"),
                Arrays.asList("merchant_Id", "B88903F1850F851C1B7591D45ECD1832", "F0F033A0FAD427CB885F35124A14F624"),
                Arrays.asList("Merchant_Id", "B52FA85E49253D497F743A81152DBAFA", "7D3CC65DDE57DD7E03260A9B51E26CD1"));


        queryParams.forEach( params -> {
            MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
            mockHttpServletRequest.setRequestURI(REQUEST_URI);
            mockHttpServletRequest.setParameter(params.get(MERCHANTID), MERCHANT_ID);
            mockHttpServletRequest.setParameter("hashValue", params.get(gwHASHVALUE));
            mockHttpServletRequest.setQueryString(MessageFormat.format("{0}={1}&hashValue={2}", params.get(MERCHANTID), MERCHANT_ID, params.get(gwHASHVALUE)));
            RedirectView actual = null;
            try {
                actual = sut.requestRedirect(mockHttpServletRequest);
            } catch (MissingServletRequestParameterException e) {
                throw new RuntimeException(e);
            }
            String expected = MessageFormat.format("http://localhost:8080/scripts/Payment/Payment.asp?{0}={1}&hashValue={2}", params.get(MERCHANTID), MERCHANT_ID, params.get(HASHVALUE));
            Assertions.assertEquals(expected, actual.getUrl());
        });

    }

    @Test
    @DisplayName("400: without hash key should return bad request")
    public void withoutHashKeyShouldReturn400() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.requestRedirect(mockHttpServletRequest));
    }

    @Test
    @DisplayName("400: with blank merchant id should return bad request")
    public void withoutMerchantIdValueShouldReturnBadRequest() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", "  ");
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.requestRedirect(mockHttpServletRequest));
    }

    @Test
    @DisplayName("400: without merchant id should return bad request")
    public void withMerchantIdShouldReturnBadRequest() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.requestRedirect(mockHttpServletRequest));
    }

    @Test
    @DisplayName("400: with invalid hash should return bad request")
    public void withInvalidHashShouldReturnBadRequest() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        mockHttpServletRequest.setParameter("hashValue", "ACDC");
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.requestRedirect(mockHttpServletRequest));
    }

    @Test
    @DisplayName("400: with unknown merchant id should return bad request")
    public void withInvalidMerchantIdReturnBadRequest() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", "GNR");
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.requestRedirect(mockHttpServletRequest));
    }

}
