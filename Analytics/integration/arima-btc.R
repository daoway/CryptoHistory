setwd('C:\\temp\\CryptoHistory\\Analytics\\integration')

source("btc.r")

library(quantmod)
library(forecast)

data <- coredata(Op(trades))

x <- tail(data, 366)

plot(x, type='l')

fit <- auto.arima(x);
fr <- forecast(fit, h=20)
plot(fr)

plot(simulate(fit,future=FALSE),col='red')
lines(x)

plot(simulate(fit,future=TRUE),col='red')
lines(x)

accuracy(fit)
adf.test(x)

acf(x)
pacf(x)
