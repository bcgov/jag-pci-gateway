package ca.bc.gov.open.jag.pcigateway.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class GatewayConfig {


    private final AppProperties appProperties;

    public GatewayConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public RestTemplate restTemplate() {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        if(StringUtils.isNotBlank(appProperties.getProxy().getHost())) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(appProperties.getProxy().getHost(), appProperties.getProxy().getPort()));
            requestFactory.setProxy(proxy);
        }

        return new RestTemplate(requestFactory);
    }

}
