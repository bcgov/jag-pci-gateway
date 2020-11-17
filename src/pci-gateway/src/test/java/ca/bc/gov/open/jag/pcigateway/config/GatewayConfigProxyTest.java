package ca.bc.gov.open.jag.pcigateway.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GatewayConfigProxyTest {

    private GatewayConfig sut;

    ApplicationContextRunner context = new ApplicationContextRunner()
            .withPropertyValues(
                    "pci-gateway.proxy.host=localhost",
                    "pci-gateway.proxy.port=8080"
            )
            .withUserConfiguration(AppProperties.class)
            .withUserConfiguration(
                    GatewayConfig.class);

    @Test
    public void checkRegisteredBeans() {


        context.run(it -> {
            assertThat(it).hasSingleBean(RestTemplate.class);
        });

    }

}
