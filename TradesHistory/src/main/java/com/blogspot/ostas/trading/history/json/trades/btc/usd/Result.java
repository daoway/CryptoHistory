
package com.blogspot.ostas.trading.history.json.trades.btc.usd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("XXBTZUSD")
    @Expose
    private List<List<String>> xXBTZUSD = null;
    @SerializedName("last")
    @Expose
    private String last;

    public List<List<String>> getXXBTZUSD() {
        return xXBTZUSD;
    }

    public void setXXBTZUSD(List<List<String>> xXBTZUSD) {
        this.xXBTZUSD = xXBTZUSD;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

}
