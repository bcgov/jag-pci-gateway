package ca.bc.gov.open.jag.pcigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "pci-gateway")
public class AppProperties {

    private String redirectUrl;

    private List<GatewayClientProperty> gatewayClients = new ArrayList<>();

    private List<GatewayRestClientProperties> gatewayRestClients = new ArrayList<>();

    private Proxy proxy;

    private int timeout = 60000;

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

    public List<GatewayRestClientProperties> getGatewayRestClients() {
        return gatewayRestClients;
    }

    public void setGatewayRestClients(List<GatewayRestClientProperties> gatewayRestClients) {
        this.gatewayRestClients = gatewayRestClients;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
