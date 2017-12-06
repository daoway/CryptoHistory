
package com.blogspot.ostas.trading.history.json.trades.btc.usd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KrakenTradesBtcUsd {

    @SerializedName("error")
    @Expose
    private List<Object> error = null;
    @SerializedName("result")
    @Expose
    private Result result;

    public List<Object> getError() {
        return error;
    }

    public void setError(List<Object> error) {
        this.error = error;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

}
