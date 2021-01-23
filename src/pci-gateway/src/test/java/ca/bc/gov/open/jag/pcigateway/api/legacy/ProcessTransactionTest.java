package ca.bc.gov.open.jag.pcigateway.api.legacy;

import ca.bc.gov.open.jag.pcigateway.api.LegacyRedirectController;
import ca.bc.gov.open.jag.pcigateway.api.RedirectController;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProcessTransactionTest {
    private static final String MERCHANT_ID = "merchantId";
    private static final String REDIRECT_URL = "http://localhost:8080";

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
