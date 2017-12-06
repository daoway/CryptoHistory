setwd('C:\\temp\\CryptoHistory\\Analytics\\integration')

source("btc.r")

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


chartSeriesTA(trades)
