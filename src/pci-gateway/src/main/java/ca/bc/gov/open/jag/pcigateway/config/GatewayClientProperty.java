package ca.bc.gov.open.jag.pcigateway.config;

public class GatewayClientProperty {

    private String MerchantId;
    private String HashKey;
    private String GatewayHashKey;

    public String getMerchantId() {
        return MerchantId;
    }

    public void setMerchantId(String merchantId) {
        MerchantId = merchantId;
    }

    public String getHashKey() {
        return HashKey;
    }

    public void setHashKey(String hashKey) {
        HashKey = hashKey;
    }

    public String getGatewayHashKey() {
        return GatewayHashKey;
    }

    public void setGatewayHashKey(String gatewayHashKey) {
        GatewayHashKey = gatewayHashKey;
    }
}
