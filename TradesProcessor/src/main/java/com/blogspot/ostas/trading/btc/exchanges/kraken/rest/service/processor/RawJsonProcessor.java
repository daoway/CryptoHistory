package com.blogspot.ostas.trading.btc.exchanges.kraken.rest.service.processor;

import com.blogspot.ostas.trading.history.json.trades.btc.usd.KrakenTradesBtcUsd;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static com.blogspot.ostas.trading.btc.exchanges.kraken.rest.common.Utils.trunkTimestamp;

public class RawJsonProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(RawJsonProcessor.class);
    public static void main(String args[]){
        String outDir = "./TradesHistory/raw-json";
        GsonBuilder gBuilder = new GsonBuilder();
        Gson gson = gBuilder.create();
        String content = null;
        KrakenTradesBtcUsd tradesFrame;
        if(! new File(outDir).exists()) throw new RuntimeException("Directory "+outDir+" doesn't exists");
        File[] files = new File(outDir).listFiles();
        List<List<String>> trades;
        for (File file : files) {
            if (file.isFile()) {
                try {
                    content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                    tradesFrame = gson.fromJson(content, KrakenTradesBtcUsd.class);
                    trades = tradesFrame.getResult().getXXBTZUSD();
                    for(final List<String> tradeInfo : trades){
                       Files.write(
                                    Paths.get("output.csv"),
                                    (
                                            String.format("%s,%s,%s",
                                                    trunkTimestamp(tradeInfo.get(2)),
                                                    //tradeInfo.get(2),
                                                    tradeInfo.get(0),
                                                    tradeInfo.get(1))

                                                    +System.lineSeparator()
                                    ).getBytes(),
                                    StandardOpenOption.CREATE,
                                    StandardOpenOption.APPEND);
                    }
                } catch (IOException e) {
                    LOG.error("Unable to read",e);
                }
                LOG.info("Processing .... "+file.getName());
                //LOG.info(content);
            }
        }
    }
}
