package ca.bc.gov.open.jag.pcigateway.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GatewayConfigTest {

    private GatewayConfig sut;

    ApplicationContextRunner context = new ApplicationContextRunner()
            .withUserConfiguration(AppProperties.class)
            .withUserConfiguration(
                    GatewayConfig.class);

    ApplicationContextRunner context2 = new ApplicationContextRunner()
            .withPropertyValues(
                    "pci-gateway.proxy.host=localhost",
                    "pci-gateway.proxy.port=8080"
            )
            .withUserConfiguration(AppProperties.class)
            .withUserConfiguration(
                    GatewayConfig.class);

    ApplicationContextRunner context3 = new ApplicationContextRunner()
            .withPropertyValues(
                    "pci-gateway.proxy.host= ",
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

        context2.run(it -> {
            assertThat(it).hasSingleBean(RestTemplate.class);
        });

        context3.run(it -> {
            assertThat(it).hasSingleBean(RestTemplate.class);
        });

    }

}
