package ca.bc.gov.open.jag.pcigateway;

import ca.bc.gov.open.jag.pcigateway.config.AppProperties;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.net.ssl.HttpsURLConnection;

@SpringBootApplication
@EnableEncryptableProperties
@EnableConfigurationProperties({AppProperties.class})
public class PciGatewayApplication {

	public static void main(String[] args) {

		// TODO: remove
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

		SpringApplication.run(PciGatewayApplication.class, args);

	}

}
