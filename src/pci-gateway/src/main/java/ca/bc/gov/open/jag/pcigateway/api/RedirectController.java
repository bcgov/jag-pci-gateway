package ca.bc.gov.open.jag.pcigateway.api;

import ca.bc.gov.open.jag.pcigateway.Keys;
import ca.bc.gov.open.jag.pcigateway.config.AppProperties;
import ca.bc.gov.open.jag.pcigateway.config.GatewayClientProperty;
import ca.bc.gov.open.jag.pcigateway.utils.QueryStringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class RedirectController {

    private Logger logger = LoggerFactory.getLogger(RedirectController.class);

    private final AppProperties appProperties;

    public RedirectController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/pcigw/Payment/Payment.asp")
    public RedirectView localRedirect(HttpServletRequest request) throws MissingServletRequestParameterException {

        GatewayClientProperty clientProperty = getGatewayClientProperty(request);

        String hashValue = request.getParameter(Keys.PARAM_TRANS_HASH_VALUE);

        if(StringUtils.isBlank(hashValue)) throw new MissingServletRequestParameterException("hashValue","string");

        if(!validateHash(getSecuredQueryString(request), clientProperty.getGatewayHashKey(), hashValue)) throw new MissingServletRequestParameterException("Hash", "Hash is invalid");

        String newHash = computeHash(getSecuredQueryString(request), clientProperty.getHashKey());

        URI redirectURI = UriComponentsBuilder.fromUri(URI.create(MessageFormat.format("{0}/{1}", appProperties.getRedirectUrl(), Keys.PAYMENT_PATH)))
                .queryParams(swapHash(request.getParameterMap(), newHash))
                .build().toUri();

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectURI.toString());

        return processRequest(request,Keys.PARAM_MERCHANT_ID,Keys.PAYMENT_PATH);
    }

    @GetMapping("/pcigw/process_transaction.asp")
    public RedirectView statusRedirect(HttpServletRequest request) throws MissingServletRequestParameterException {
        GatewayClientProperty clientProperty = getGatewayClientProperty(request);

        return processRequest(request,Keys.PARAM_CAMEL_MERCHANT_ID,Keys.PROCESS_TRANSACTION_PATH);

    }

    private RedirectView processRequest(HttpServletRequest request, String requestIdKey, String requestPath) throws MissingServletRequestParameterException {
        GatewayClientProperty clientProperty = getGatewayClientProperty(request, requestIdKey);

        String hashValue = request.getParameter(Keys.PARAM_TRANS_HASH_VALUE);

        if(StringUtils.isBlank(hashValue)) throw new MissingServletRequestParameterException("hashValue","string");

        if(!validateHash(getSecuredQueryString(request), clientProperty.getGatewayHashKey(), hashValue)) throw new MissingServletRequestParameterException("Hash", "Hash is invalid");

        String newHash = computeHash(getSecuredQueryString(request), clientProperty.getHashKey());

        URI redirectURI = UriComponentsBuilder.fromUri(URI.create(MessageFormat.format("{0}/{1}", appProperties.getRedirectUrl(), requestPath)))
                .queryParams(swapHash(request.getParameterMap(), newHash))
                .build().toUri();

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectURI.toString());

        return redirectView;
    }

    private String getSecuredQueryString(HttpServletRequest request) {
        return StringUtils.substringBeforeLast(request.getQueryString(), "&" + Keys.PARAM_TRANS_HASH_VALUE);
    }

    private boolean validateHash(String queryString, String hashKey, String hashValue) {
        // Compute the hash
        return StringUtils.equalsIgnoreCase(hashValue, computeHash(queryString, hashKey));

    }

    private GatewayClientProperty getGatewayClientProperty(HttpServletRequest request) throws MissingServletRequestParameterException {

        Optional<String> merchantIdKey = QueryStringUtils.getMerchantId(request.getParameterNames());

        if(!merchantIdKey.isPresent())
            throw new MissingServletRequestParameterException("Property", "merchantId is required");

        String merchantId = request.getParameter(merchantIdKey.get());

        if(StringUtils.isBlank(merchantId))
            throw new MissingServletRequestParameterException("Property", "merchantId is required");

        Optional<GatewayClientProperty> clientProperty = this.appProperties.getGatewayClients()
                .stream()
                .filter(x -> StringUtils.equals(x.getMerchantId(), merchantId))
                .findFirst();

        if(!clientProperty.isPresent()) {
            throw new MissingServletRequestParameterException("Property", "merchantId invalid");
        }

        return clientProperty.get();
    }

    private String computeHash(String value, String hashKey) {
        return DigestUtils.md5Hex(MessageFormat.format("{0}{1}", value, hashKey)).toUpperCase();
    }

    private MultiValueMap<String, String> swapHash(Map<String, String[]> current, String hash) {

        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();

        current.entrySet()
                .stream()
                .forEach(entry -> result.put(entry.getKey(), Arrays.asList(entry.getValue())));

        result.set(Keys.PARAM_TRANS_HASH_VALUE, hash);

        return result;

    }
}
