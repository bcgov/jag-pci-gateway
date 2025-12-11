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
        mockHttpServletRequest.setParameter("hashValue", "CF7CFE6CA7AED24249DA5E7C7C465B49C9B0BDDB69F875F4F4AC863CEDA1E085");
        mockHttpServletRequest.setQueryString("merchant_id=merchantId&hashValue=CF7CFE6CA7AED24249DA5E7C7C465B49C9B0BDDB69F875F4F4AC863CEDA1E085");
        RedirectView result = sut.requestRedirect(mockHttpServletRequest);

        Assertions.assertEquals("http://localhost:8080/scripts/Payment/Payment.asp?merchant_id=merchantId&hashValue=3503fe6789f9552efebf39de20b1735c9a969ebe9a6991d4164b2575fdeac723", result.getUrl());

    }

    @Test
    @DisplayName("200: with valid parameters and additional should rebuild url")
    public void withValidParamsAndMoreShouldReturnValidUrl() throws MissingServletRequestParameterException {

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI(REQUEST_URI);
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        mockHttpServletRequest.setParameter("hashValue", "CF7CFE6CA7AED24249DA5E7C7C465B49C9B0BDDB69F875F4F4AC863CEDA1E085");
        mockHttpServletRequest.setParameter("otherparams", "otherparams");
        mockHttpServletRequest.setQueryString(MessageFormat.format("merchant_id=merchantId&hashValue={0}&otherparams=otherparams", "CF7CFE6CA7AED24249DA5E7C7C465B49C9B0BDDB69F875F4F4AC863CEDA1E085"));
        RedirectView result = sut.requestRedirect(mockHttpServletRequest);

        Assertions.assertEquals("http://localhost:8080/scripts/Payment/Payment.asp?merchant_id=merchantId&hashValue=3503fe6789f9552efebf39de20b1735c9a969ebe9a6991d4164b2575fdeac723&otherparams=otherparams", result.getUrl());
    }

    @Test
    @DisplayName("200: test variation of merchant it")
    public void testVariationOfMerchantId() throws MissingServletRequestParameterException {


        final int MERCHANTID = 0;
        final int gwHASHVALUE = 1;  // USING gateway-hash-key TO COMPUTE HASHVALUE
        final int HASHVALUE = 2;    // USING hash-key TO COMPUTE HASHVALUE

        List<List<String>> queryParams = Arrays.asList(
                Arrays.asList("merchantid", "9bc3ec6cbc611883eef296c8f1b63a3521d74da00034a1c9d7321546edb98e18", "c81a3d3430c80798d5b89f44b0890d92c3f9179fba3a64c16ca768a521014ede"),
                Arrays.asList("Merchantid", "43aec5cd2eb8983532625047ac9e6f4f38a9a0eed1b3b3639dae17d382157fd3", "0c4bd80823d3e11887a46109e1f01e539cef0ecada993cbfa503c1f1ec67dc06"),
                Arrays.asList("merchantId", "1588b33d9373a0c5b8508a360458919c6d8ec6c691b6594f663abccf110b2d15", "60ed128ba57c17c64e5e94f087aaa1dfa765c827ce9ed04e82556f09c7698968"),
                Arrays.asList("MerchantId", "38afa44abf609e918c5edb3512fc1b7c135e682e8f443ce50dd9ccd634cf03bc", "9caaae253df231a367e015cb6fa2c9a8b1f4e214d6df76a38d724da03fe53772"),
                Arrays.asList("merchant_id", "cf7cfe6ca7aed24249da5e7c7c465b49c9b0bddb69f875f4f4ac863ceda1e085", "3503fe6789f9552efebf39de20b1735c9a969ebe9a6991d4164b2575fdeac723"),
                Arrays.asList("Merchant_id", "9925496503e3c50e6a730428a06ab57525d2b166c8c0a77fb48973aa0ac1d0a2", "741ae352f285eb7c0b1e197f185d49f245762973c72c048e92da751b4426effe"),
                Arrays.asList("merchant_Id", "e70d8ab94df4fde6046a566ce6d0d2f9a5bb9dc595b6f80b40d6890e305dc9a7", "eb4b1c03a1fb62964be9ab8e3f3060f4de6443a01377a9fcce24d881f8b4a3cd"),
                Arrays.asList("Merchant_Id", "c17e25b020f4209f6849fbbcef347f972079db145128ec8cae9a727ad93c9def", "7e98c3edaaa11c678c49318207fe8b76efd0f383d5977f01f3885b0aaedb9bc5"));


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
