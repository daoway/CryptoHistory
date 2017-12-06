package com.blogspot.ostas.trading.history;

import com.google.gson.Gson;
import com.blogspot.ostas.trading.history.json.trades.btc.usd.KrakenTradesBtcUsd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpPoolingService {
    private static final Logger log = LoggerFactory.getLogger(HttpPoolingService.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RetryTemplate retryTemplate;
    @Autowired
    private ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor;
    @Autowired
    private Gson gson;
    @Autowired
    private PersistentJsonService persistentJsonService;

    private final String KRAKEN_REST_TRADES_URL = "https://api.kraken.com/0/public/Trades?pair=%s&since=%s";
    private final long delayBetweenRestCalls = 500;
    private String pair = "XXBTZUSD";
    private String since ="0";

    @Scheduled(fixedDelay = delayBetweenRestCalls)
    public void fetchData() {
        final String url = String.format(KRAKEN_REST_TRADES_URL,pair,since);
        retryTemplate.execute(context ->{
            final String result = restTemplate.getForObject(url, String.class);
            log.info(result);
            final KrakenTradesBtcUsd tradesFrame = gson.fromJson(result, KrakenTradesBtcUsd.class);
            if(!tradesFrame.getError().isEmpty()) {
                //in case of error will throw exception to trigger retry
                throw new RuntimeException("Maybe service unavailable ?");
            }else{
                String path = String.format("kraken-%s-trades-%s.json",pair,since);
                persistentJsonService.persist(path,result.getBytes());
                //ready for next iteration
                since = tradesFrame.getResult().getLast();
                //no error and then will pull data until we'll receive []
                if(tradesFrame.getResult().getXXBTZUSD().isEmpty()) {
                    scheduledAnnotationBeanPostProcessor.destroy();
                }
            }
            return null;
        });
    }
}
