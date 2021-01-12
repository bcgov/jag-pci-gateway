package ca.bc.gov.open.jag.pcigateway.config;

public class GatewayRestClientProperties {
    private String MerchantId;
    private String ApiKey;
    private String GatewayApiKey;

    public String getMerchantId() {
        return MerchantId;
    }

    public void setMerchantId(String merchantId) {
        MerchantId = merchantId;
    }

    public String getApiKey() {
        return ApiKey;
    }

    public void setApiKey(String apiKey) {
        ApiKey = apiKey;
    }

    public String getGatewayApiKey() {
        return GatewayApiKey;
    }

    public void setGatewayApiKey(String gatewayApiKey) {
        GatewayApiKey = gatewayApiKey;
    }

}
