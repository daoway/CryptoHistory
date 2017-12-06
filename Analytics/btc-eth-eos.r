setwd('C:\\temp\\CryptoHistory\\Analytics')

library(quantmod);
library(forecast)
library(tseries)

setSymbolLookup(
  KrakenBtcUsd  = list(src="csv"),
  KrakenEthUsd  = list(src="csv"),
  KrakenEosEth  = list(src="csv"),
  KrakenBchUsd  = list(src="csv"),
    format="yyyy-MM-ddTHH:mm:ss.fffZ");

KrakenBtcUsd <-  getSymbols("KrakenBtcUsd", auto.assign=FALSE);
KrakenEthUsd <-  getSymbols("KrakenEthUsd", auto.assign=FALSE);
KrakenEosEth <-  getSymbols("KrakenEosEth", auto.assign=FALSE);

KrakenBchUsd <-  getSymbols("KrakenBchUsd", auto.assign=FALSE);

chartSeriesTA <- function(symb){
  chartSeries(symb, subset='last 30 days',
              TA=c(
                addVo(),
                addSMA(), 
                addEMA(),
                addMACD(),
                addRSI(),
                addSMI(),
                addBBands()) 
  );
  addTA(ZigZag(symb[,2:3],5),on=1);
}


chartSeriesTA(KrakenBtcUsd)
chartSeriesTA(KrakenEthUsd)
chartSeriesTA(KrakenEosEth)

tsLimit <- 620;

KrakenBtcUsdOpenPrices <- tail(
    coredata(
        Op(KrakenBtcUsd)
      ),tsLimit
    );

KrakenEthUsdOpenPrices <- tail(
  coredata(
    Op(KrakenEthUsd)
  ),tsLimit
);

KrakenEosEthOpenPrices <- tail(
  coredata(
    Op(KrakenEosEth)
  ),tsLimit
);

KrakenBchUsdOpenPrices <- tail(
  coredata(
    Op(KrakenBchUsd)
  ),tsLimit
);

cor(KrakenBtcUsdOpenPrices, KrakenEthUsdOpenPrices, method = c("pearson", "kendall", "spearman"))
cor.test(KrakenBtcUsdOpenPrices, KrakenEthUsdOpenPrices, method=c("pearson", "kendall", "spearman"))

cor(KrakenEthUsdOpenPrices, KrakenEosEthOpenPrices,  method = c("pearson", "kendall", "spearman"))
cor.test(KrakenEthUsdOpenPrices, KrakenEosEthOpenPrices, method=c("pearson", "kendall", "spearman"))

#cor(KrakenBtcUsdOpenPrices, KrakenEosEthOpenPrices,  method = c("pearson", "kendall", "spearman"))
#cor.test(KrakenBtcUsdOpenPrices, KrakenEosEthOpenPrices, method=c("pearson", "kendall", "spearman"))


cor(KrakenBtcUsdOpenPrices, KrakenBchUsdOpenPrices, method = c("pearson", "kendall", "spearman"))
cor.test(KrakenBtcUsdOpenPrices, KrakenBchUsdOpenPrices, method=c("pearson", "kendall", "spearman"))

