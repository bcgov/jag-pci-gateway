package ca.bc.gov.open.jag.pcigateway;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class PciGatewayApplication {

	public static void main(String[] args) {

		SpringApplication.run(PciGatewayApplication.class, args);

	}

}
