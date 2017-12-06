setwd('C:\\temp\\CryptoHistory\\Analytics\\integration')

library(quantmod);
library(forecast)
library(tseries)

setSymbolLookup(
  KrakenBtcUsd  = list(src="csv"), format="yyyy-MM-ddTHH:mm:ss.fffZ");

trades <-  getSymbols("KrakenBtcUsd", auto.assign=FALSE);


