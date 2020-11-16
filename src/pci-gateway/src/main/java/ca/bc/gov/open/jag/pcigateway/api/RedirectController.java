package ca.bc.gov.open.jag.pcigateway.api;

import ca.bc.gov.open.jag.pcigateway.Keys;
import ca.bc.gov.open.jag.pcigateway.config.AppProperties;
import ca.bc.gov.open.jag.pcigateway.config.GatewayClientProperty;
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

    @GetMapping("/pcigw/payment/payment.asp")
    public RedirectView localRedirect(HttpServletRequest request) throws MissingServletRequestParameterException {

        GatewayClientProperty clientProperty = getGatewayClientProperty(request);

        String hashValue = request.getParameter(Keys.PARAM_TRANS_HASH_VALUE);

        if(StringUtils.isBlank(hashValue)) throw new MissingServletRequestParameterException("hashValue","string");

        if(!validateHash(getSecuredQueryString(request), clientProperty.getGatewayHashKey(), hashValue)) throw new MissingServletRequestParameterException("Hash", "Hash is invalid");

        String newHash = computeHash(getSecuredQueryString(request), clientProperty.getHashKey());

        URI redirectURI = UriComponentsBuilder.fromUri(URI.create(appProperties.getRedirectUrl()))
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

        Optional<GatewayClientProperty> clientProperty = this.appProperties.getGatewayClients()
                .stream()
                .filter(x -> StringUtils.equals(x.getMerchantId(), request.getParameter(Keys.PARAM_MERCHANT_ID)))
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
                .filter(x -> !StringUtils.equals(x.getKey(), Keys.PARAM_TRANS_HASH_VALUE))
                .forEach(entry -> result.put(entry.getKey(), Arrays.asList(entry.getValue())));

        result.add(Keys.PARAM_TRANS_HASH_VALUE, hash);

        return result;

    }
}
