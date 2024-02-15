package ca.bc.gov.open.jag.pcigateway.api;

import ca.bc.gov.open.jag.pcigateway.Keys;
import ca.bc.gov.open.jag.pcigateway.config.AppProperties;
import ca.bc.gov.open.jag.pcigateway.config.GatewayClientProperty;
import ca.bc.gov.open.jag.pcigateway.utils.HttpServletRequestUtils;
import ca.bc.gov.open.jag.pcigateway.utils.QueryStringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Optional;

@Controller
@RequestMapping("/pcigw/")
public class LegacyRedirectController {

    private Logger logger = LoggerFactory.getLogger(LegacyRedirectController.class);

    private final AppProperties appProperties;
    private final RestTemplate restTemplate;

    public LegacyRedirectController(AppProperties appProperties, RestTemplate restTemplate) {
        this.appProperties = appProperties;
        this.restTemplate = restTemplate;
    }

    @GetMapping({"/{[P-p]ayment}/{[P-p]ayment\\.asp","/{[P-p]ayment[P-p]rofile}/{[W-w]ebform\\.asp}"})
    public RedirectView requestRedirect(HttpServletRequest request) throws MissingServletRequestParameterException {

        logger.info("received new redirect request");
        logger.warn("this is legacy and will be removed");

        return redirectRequest(request);

    }

    @GetMapping("/process_transaction.asp")
    public ResponseEntity<String> statusRedirect(HttpServletRequest request) throws MissingServletRequestParameterException {

        logger.info("received new process transaction proxy request");
        logger.warn("this is legacy and will be removed");

        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(processRequest(request), String.class);

        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            logger.info("Request for process transaction succeeded");
        } else {
            logger.error("Request for process transaction failed with status code {}", responseEntity.getStatusCodeValue());
        }

        return responseEntity;

    }

    private RedirectView redirectRequest(HttpServletRequest request) throws MissingServletRequestParameterException {

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(processRequest(request).toString());

        logger.info("redirect path successfully generated");

        return redirectView;

    }

    private URI processRequest(HttpServletRequest request) throws MissingServletRequestParameterException {

        GatewayClientProperty clientProperty = getGatewayClientProperty(request);

        if(StringUtils.isBlank(request.getParameter(Keys.PARAM_HASH_VALUE)))
            throw new MissingServletRequestParameterException("hashValue","string");

        if(!validateHash(getSecuredQueryString(request), clientProperty.getGatewayHashKey(), request.getParameter(Keys.PARAM_HASH_VALUE)))
            throw new MissingServletRequestParameterException("Hash", "Hash is invalid");

        URI uri = UriComponentsBuilder
                .fromUri(URI.create(MessageFormat.format("{0}/{1}", appProperties.getRedirectUrl(), request.getRequestURI().replace(Keys.PCIGW, Keys.SCRIPTS))))
                .queryParams(QueryStringUtils.setParam(request.getParameterMap(), Keys.PARAM_HASH_VALUE,
                        computeHash(getSecuredQueryString(request), clientProperty.getHashKey())))
                .build().toUri();

        String queryStringForHashValue = StringUtils.substringBeforeLast(uri.getRawQuery(), "&" + Keys.PARAM_HASH_VALUE);

        uri = UriComponentsBuilder
                .fromUri(URI.create(MessageFormat.format("{0}/{1}", appProperties.getRedirectUrl(), request.getRequestURI().replace(Keys.PCIGW, Keys.SCRIPTS))))
                .queryParams(QueryStringUtils.setParam(request.getParameterMap(), Keys.PARAM_HASH_VALUE,
                        computeHash(queryStringForHashValue, clientProperty.getHashKey())))
                .build().toUri();

        return uri;

    }

    private String getSecuredQueryString(HttpServletRequest request) {
        return StringUtils.substringBeforeLast(request.getQueryString(), "&" + Keys.PARAM_HASH_VALUE);
    }

    private boolean validateHash(String queryString, String hashKey, String hashValue) {
        return StringUtils.equalsIgnoreCase(hashValue, computeHash(queryString, hashKey));
    }

    private GatewayClientProperty getGatewayClientProperty(HttpServletRequest request) throws MissingServletRequestParameterException {

        Optional<String> merchantId = HttpServletRequestUtils.getMerchantId(request);

        if(!merchantId.isPresent())
            throw new MissingServletRequestParameterException("Property", "merchantId is required");

        Optional<GatewayClientProperty> clientProperty = this.appProperties.getGatewayClients()
                .stream()
                .filter(x -> StringUtils.equals(x.getMerchantId(), merchantId.get()))
                .findFirst();

        if(!clientProperty.isPresent()) {
            throw new MissingServletRequestParameterException("Property", "merchantId invalid");
        }

        return clientProperty.get();
    }

    private String computeHash(String value, String hashKey) {
        return DigestUtils.md5Hex(MessageFormat.format("{0}{1}", value, hashKey)).toUpperCase();
    }

}
