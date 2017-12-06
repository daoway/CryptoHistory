package com.blogspot.ostas.trading.btc.exchanges.kraken.rest.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    static String timestampPattern = "(\\d+)(\\.)?(\\d+)";
    static Pattern r = Pattern.compile(timestampPattern);

    public static String trunkTimestamp(String timestampWithMs){
        final Matcher m = r.matcher(timestampWithMs);
        m.find();
        return m.group(1);
    }
}
