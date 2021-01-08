package ca.bc.gov.open.jag.pcigateway.api;

import ca.bc.gov.open.jag.pcigateway.Keys;
import ca.bc.gov.open.jag.pcigateway.config.AppProperties;
import ca.bc.gov.open.jag.pcigateway.config.GatewayRestClientProperties;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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


    @PostMapping("/payments")
    public ResponseEntity<String> statusRedirect(HttpServletRequest request,
                                                 @RequestHeader("Authorization") String passcode,
                                                 @RequestBody String body) {
        logger.info("received new process transaction proxy request");

        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(MessageFormat.format("{0}{1}", appProperties.getRedirectUrl() ,request.getRequestURI().replace(Keys.PCIGW, Keys.REST)), processRequest(passcode, body), String.class);

        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            logger.info("Request for process transaction succeeded");
        } else {
            logger.error("Request for process transaction failed with status code {}", responseEntity.getStatusCodeValue());
        }

        return responseEntity;
    }

    private HttpEntity<String> processRequest(String passcode, String body) {

        String key = passcode.replace("Passcode ", "");

        List<String> keys = Arrays.asList(new String(Base64.getDecoder().decode(key)).split(":"));

        Optional<GatewayRestClientProperties> properties = appProperties.getGatewayRestClients().stream()
                .filter(property -> property.getMerchantId().equals(keys.get(0)) && property.getGatewayApiKey().equals(keys.get(1))).findFirst();

        if (!properties.isPresent()) throw new RuntimeException("NO");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",MessageFormat.format("Passcode {0}", Base64.getEncoder().encodeToString(MessageFormat.format("{0}:{1}", properties.get().getMerchantId(),properties.get().getApiKey()).getBytes())));

        return new HttpEntity<>(body, headers);
    }
}
