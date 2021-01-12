package ca.bc.gov.open.jag.pcigateway.api;

import ca.bc.gov.open.jag.pcigateway.Keys;
import ca.bc.gov.open.jag.pcigateway.config.AppProperties;
import ca.bc.gov.open.jag.pcigateway.config.GatewayRestClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;

@Controller
@RequestMapping("/pcigw/")
public class RestProxyController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AppProperties appProperties;
    private final RestTemplate restTemplate;

    public RestProxyController(AppProperties appProperties, RestTemplate restTemplate) {
        this.appProperties = appProperties;
        this.restTemplate = restTemplate;
    }


    @PostMapping("/rest/{resource}")
    public ResponseEntity<String> postProxy(@RequestHeader("Authorization") String passcode,
                                            @PathVariable("resource") String resource,
                                            @RequestBody String body) {
        logger.info("received new post proxy request");

        try {
            return this.restTemplate.postForEntity(MessageFormat.format("{0}{1}", appProperties.getApiUrl(), resource), processRequest(passcode, body), String.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode().value()).body(e.getResponseBodyAsString());
        }

    }

    @DeleteMapping("/rest/profiles/{profileId}")
    public ResponseEntity<String> deleteProxy(HttpServletRequest request,
                                              @RequestHeader("Authorization") String passcode) {
        logger.info("received new delete proxy request");
        try {
            return this.restTemplate.exchange(MessageFormat.format("{0}{1}", appProperties.getApiUrl(), request.getRequestURI().replace(Keys.PCIGW, Keys.REST)), HttpMethod.DELETE, processRequest(passcode,""), String.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode().value()).body(e.getResponseBodyAsString());
        }
    }

    private HttpEntity<String> processRequest(String passcode, String body) {

        String key = passcode.replace("Passcode ", "");

        String[] keys  = new String(Base64.getDecoder().decode(key)).split(":");

        if (keys.length != 2) throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);

        Optional<GatewayRestClientProperties> properties = appProperties.getGatewayRestClients().stream()
                .filter(property -> property.getMerchantId().equals(keys[0]) && property.getGatewayApiKey().equals(keys[1])).findFirst();

        if (!properties.isPresent()) throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", MessageFormat.format("Passcode {0}", Base64.getEncoder().encodeToString(MessageFormat.format("{0}:{1}", properties.get().getMerchantId(),properties.get().getApiKey()).getBytes())));

        return new HttpEntity<>(body, headers);
    }
}
