package ca.bc.gov.open.jag.pcigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "pci-gateway")
public class AppProperties {

    private String redirectUrl;

    private List<GatewayClientProperty> gatewayClients = new ArrayList<>();

    private Proxy proxy;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public List<GatewayClientProperty> getGatewayClients() {
        return gatewayClients;
    }

    public void setGatewayClients(List<GatewayClientProperty> gatewayClients) {
        this.gatewayClients = gatewayClients;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
