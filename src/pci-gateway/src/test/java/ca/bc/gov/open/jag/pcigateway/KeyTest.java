package ca.bc.gov.open.jag.pcigateway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KeyTest {

    @Test
    public void test() {

        Assertions.assertEquals("merchantId", Keys.PARAM_MERCHANT_ID);
        Assertions.assertEquals("hashValue", Keys.PARAM_TRANS_HASH_VALUE);

    }

}
