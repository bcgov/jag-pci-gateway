package ca.bc.gov.open.jag.pcigateway;

import ca.bc.gov.open.jag.pcigateway.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class PciGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PciGatewayApplication.class, args);
	}

}
