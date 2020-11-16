package ca.bc.gov.open.jag.pcigateway.utils;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
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


    public static MultiValueMap<String, String> setParam(Map<String, String[]> current, String key, String value) {

            MultiValueMap<String, String> result = new LinkedMultiValueMap<>();

            current.entrySet()
                    .stream()
                    .forEach(entry -> result.put(entry.getKey(), Arrays.asList(entry.getValue())));

            result.set(key, value);

            return result;


    }

}
