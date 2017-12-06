package com.blogspot.ostas.history;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.apache.spark.sql.functions.*;


/**
 * Created by daoway on 5/20/2017.
 */
public class OHLCFileProducer {
    public static final Logger logger = LoggerFactory.getLogger(OHLCFileProducer.class);
    public static void main(String[] args){
        logger.info("Starting processing....");
        SparkSession spark = SparkSession.builder().master("local")
                .appName("Spark-TS Example")
                //.config("spark.driver.memory", "1g")
                .getOrCreate();
        //spark.sparkContext().setLogLevel("WARN");

        List<StructField> fields = new ArrayList();
        fields.add(DataTypes.createStructField("timestamp", DataTypes.LongType, true));
        fields.add(DataTypes.createStructField("price", DataTypes.DoubleType, true));
        fields.add(DataTypes.createStructField("amount", DataTypes.DoubleType, true));
        StructType schema = DataTypes.createStructType(fields);

        Dataset<Row> df = spark.read()
                .format("csv")
                .option("header", "false") //reading the headers
                .option("mode", "DROPMALFORMED")
                .schema(schema)
                .csv("output.csv");

        System.out.println("========== Print Schema ============");
        df.printSchema();
        System.out.println("========== Print Data ==============");
        df.show();
        df.rdd().cache();
        df.registerTempTable("history");

        Dataset<Row> out = df.withColumn("timestamp",from_unixtime(col("timestamp"))).groupBy(window(col("timestamp"),"1 hour").alias("date"))
                .agg(
                        first("price").alias("open"),
                        max("price").alias("high"),
                        min("price").alias("low"),
                        last("price").alias("close"),
                        round(sum("amount"),6).alias("volume") //not good but okay...
                ).orderBy("date");

        out.show();
        out.rdd().cache();

//        "Index","SPY.Open","SPY.High","SPY.Low","SPY.Close","SPY.Volume","SPY.Adjusted"
        String exchange = "BTCE";
        out.select(
                col("date.start").alias("Index"),
                col("open").alias(exchange+".Open"),
                col("high").alias(exchange+".High"),
                col("low").alias(exchange+".Low"),
                col("close").alias(exchange+".Close"),
                col("volume").alias(exchange+".Volume"),
                col("close").alias(exchange+".Adjusted"))
                .repartition(1)
                .write()
                .mode("overwrite")
                .format("csv")
                .option("header", "true")
                .save("ohlc-for-r.csv");

        /*
        out.select(
                col("date.start").alias("date"),
                col("open"),
                col("high"),
                col("low"),
                col("close"),
                col("volume"))
                .repartition(1)
                .write()
                .mode("overwrite")
                .format("csv")
                .option("header", "true")
                .save("ohlc-raw.csv");
*/
        spark.close();
    }
}

