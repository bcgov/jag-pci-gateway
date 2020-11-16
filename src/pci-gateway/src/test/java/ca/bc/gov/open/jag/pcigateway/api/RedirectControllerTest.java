package ca.bc.gov.open.jag.pcigateway.api;

import ca.bc.gov.open.jag.pcigateway.config.AppProperties;
import ca.bc.gov.open.jag.pcigateway.config.GatewayClientProperty;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedirectControllerTest {

    private static final String MERCHANT_ID = "merchantId";
    private static final String REDIRECT_URL = "http://localhost:8080";

    private RedirectController sut;

    @BeforeAll
    public void beforeAll() {

        AppProperties appProperties = new AppProperties();
        appProperties.setRedirectUrl(REDIRECT_URL);
        List<GatewayClientProperty> clients = new ArrayList<>();
        GatewayClientProperty testClient = new GatewayClientProperty();
        testClient.setMerchantId(MERCHANT_ID);
        testClient.setHashKey("1234");
        testClient.setGatewayHashKey("5678");
        clients.add(testClient);
        appProperties.setGatewayClients(clients);
        sut = new RedirectController(appProperties);

    }

    @Test
    @DisplayName("200: with valid parameters should rebuild url")
    public void withValidParamsShouldReturnValidUrl() throws MissingServletRequestParameterException {

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");

        RedirectView result = sut.localRedirect(mockHttpServletRequest);

        Assertions.assertEquals("http://localhost:8080?merchant_id=merchantId&hashValue=E2EEA71D02D92AD968A9A63A44862413", result.getUrl());

    }

    @Test
    @DisplayName("200: with valid parameters and additional should rebuild url")
    public void withValidParamsAndMoreShouldReturnValidUrl() throws MissingServletRequestParameterException {

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");
        mockHttpServletRequest.setParameter("otherparams", "otherparams");

        RedirectView result = sut.localRedirect(mockHttpServletRequest);

        Assertions.assertEquals("http://localhost:8080?merchant_id=merchantId&hashValue=E2EEA71D02D92AD968A9A63A44862413&otherparams=otherparams", result.getUrl());

    }

    @Test
    @DisplayName("400: without hash key should return bad request")
    public void withoutHashKeyShouldReturn400() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("merchant_id", MERCHANT_ID);
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.localRedirect(mockHttpServletRequest));
    }

    @Test
    @DisplayName("400: without merchant id should return bad request")
    public void withMerchantIdShouldReturnBadRequest() throws MissingServletRequestParameterException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("hashValue", "810AB4ECB7C361D2FCEEEABD2F7994EA");
        Assertions.assertThrows(MissingServletRequestParameterException.class, () ->  sut.localRedirect(mockHttpServletRequest));
    }




}
