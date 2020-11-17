package ca.bc.gov.open.jag.pcigateway.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class HttpServletRequestUtils {

    private HttpServletRequestUtils() {}

    public static Optional<String> getMerchantId(HttpServletRequest httpServletRequest) {

        Optional<String> merchantIdKey = QueryStringUtils.getMerchantId(httpServletRequest.getParameterNames());

        if(!merchantIdKey.isPresent()) return Optional.empty();

        String merchantId = httpServletRequest.getParameter(merchantIdKey.get());

        return StringUtils.isBlank(merchantId) ? Optional.empty() : Optional.of(merchantId);

    }


}
