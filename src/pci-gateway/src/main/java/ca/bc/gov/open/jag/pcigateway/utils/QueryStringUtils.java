package ca.bc.gov.open.jag.pcigateway.utils;

import java.util.Enumeration;
import java.util.Optional;

public class QueryStringUtils {

    private QueryStringUtils() {}

    public static Optional<String> getMerchantId(Enumeration<String> parametersName) {

        while (parametersName.hasMoreElements()) {

            String value = parametersName.nextElement();

            if(value.matches("^[M,m]erchant[_]{0,1}[I,i]d$")) return Optional.of(value);

        }

        return Optional.empty();
    }

}
