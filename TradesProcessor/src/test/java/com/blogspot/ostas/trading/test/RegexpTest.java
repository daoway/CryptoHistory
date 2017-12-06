package com.blogspot.ostas.trading.test;

import org.junit.Test;

import static com.blogspot.ostas.trading.btc.exchanges.kraken.rest.common.Utils.trunkTimestamp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by daoway on 11/28/2017.
 */
public class RegexpTest {
    @Test
    public void trunkTimestampTest(){
        String timestampWithMs = "1391153715.1029";
        assertEquals("1391153715", trunkTimestamp(timestampWithMs));
        assertNotEquals("1029",trunkTimestamp(timestampWithMs));
    }
}
