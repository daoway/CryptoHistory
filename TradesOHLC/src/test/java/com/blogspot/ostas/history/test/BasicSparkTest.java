package com.blogspot.ostas.history.test;

import org.apache.commons.io.FileUtils;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.apache.spark.sql.types.DataTypes.*;
import static org.junit.Assert.*;

import static org.apache.spark.sql.functions.*;
public class BasicSparkTest {
    public static final Logger logger = LoggerFactory.getLogger(BasicSparkTest.class);
    Dataset<Row> df;//= spark.createDataFrame(data, schema);
    private transient SparkSession spark;

    @Before
    public void setUp() {
        //System.setProperty("hadoop.home.dir","src\\main\\resources");
        spark = SparkSession.builder()
                .master("local[*]")
                .appName("testing")
                .config("spark.ui.enabled", false)
                .getOrCreate();
        /*
            data set could have equal timestamps
         */
        List<Row> data = Arrays.asList(
                RowFactory.create(1313331280L, 10.4, 0.779),
                RowFactory.create(1313334917L, 10.4, 0.101),
                RowFactory.create(1313334917L, 10.4, 0.316),
                RowFactory.create(1313340309L, 10.5, 1.8),
                RowFactory.create(1313340309L, 10.5, 0.15),
                RowFactory.create(1313340895L, 10.5, 2.11),
                RowFactory.create(1313340895L, 10.5, 4.89),
                RowFactory.create(1313341133L, 10.5, 0.43),
                RowFactory.create(1313341218L, 10.5, 1.04),
                RowFactory.create(1313341475L, 10.5, 2.78838),
                RowFactory.create(1313341475L, 10.5, 0.5),
                RowFactory.create(1313341717L, 11.0, 1.2248),
                RowFactory.create(1313341814L, 10.9, 0.585827),
                RowFactory.create(1313341877L, 11.0, 0.438),
                RowFactory.create(1313347394L, 10.0, 0.2),
                RowFactory.create(1313379090L, 11.0, 0.16),
                RowFactory.create(1313380187L, 10.0, 0.643451),
                RowFactory.create(1313380320L, 10.0, 2.0),
                RowFactory.create(1313380522L, 10.0, 0.435),
                RowFactory.create(1313382686L, 10.0, 2.28356)
        );
        StructType schema = new StructType(new StructField[]{
                createStructField("timestamp", LongType, false),
                createStructField("price", DoubleType, false),
                createStructField("amount", DoubleType, false)
        });

        df = spark.createDataFrame(data, schema);
    }

    @After
    public void tearDown() {
        spark.stop();
        spark = null;
    }

    @Test
    public void countTest() {
        df.registerTempTable("history");

        Dataset<Row> sqlDF = spark.sql("select count(1) from history");
        sqlDF.show();
        long count = sqlDF.first().getLong(0);

        assertEquals(20, count);
    };

    @Test
    public void groupByTest() {
        df.registerTempTable("history");
        //Dataset<Row> sqlDF = spark.sql("select timestamp, first(price) as open, last(price) as close, sum(amount) as volume, min(price) as low, max(price) as high from history group by timestamp,price,amount");
        Dataset<Row> sqlDF = spark.sql("select * from history");
        //sqlDF.withColumn("timestamp",from_unixtime(col("timestamp")));
        Dataset<Row> out = sqlDF.withColumn("timestamp",from_unixtime(col("timestamp"))).groupBy(window(col("timestamp"),"1 hour").alias("date"))
                .agg(
                    first("price").alias("open"),
                    max("price").alias("high"),
                    min("price").alias("low"),
                    last("price").alias("close"),
                    round(sum("amount"),6).alias("volume") //not good but okay...
        ).orderBy("date");

        out.printSchema();

        //out = out.drop("ddate");

        out.show();

        out.select(
                col("date.start").alias("date"),
                col("open"),
                col("high"),
                col("low"),
                col("close"),
                col("volume")
        ).repartition(1)
                .write()
                .mode("overwrite")
                .format("csv")
                .option("header", "true")
                .save("ohlc.csv");

        out.rdd().cache();

        List<Row> rows = out.select("open").collectAsList();
        double[] openPrices = new double[rows.size()];
        for(int i=0;i<rows.size();i++){
            openPrices[i] = rows.get(i).getDouble(0);
        }
        System.out.println(openPrices);
        Vector tsvector = Vectors.dense(openPrices);
        System.out.println("Ts vector:" + tsvector.toString());
    }

    @Test
    public void betweenTest(){
        df.registerTempTable("history");
        //Dataset<Row> sqlDF = spark.sql("select * from history where timestamp between 1313331280 and 1313340309");
        Dataset<Row> sqlDF = spark.sql("select * from history").filter(col("timestamp").between(1313331280,1313340309));
        sqlDF.show();
        int howmany = sqlDF.collectAsList().size();
        assertEquals(5,howmany);
    }
    /*
        System.setProperty("hadoop.home.dir", "full path to the bin folder with winutils");
        Download winutils.exe from http://public-repo-1.hortonworks.com/hdp-win-alpha/winutils.exe.
        SetUp your HADOOP_HOME environment variable on the OS level or programmatically:
     */
    @Test @Ignore
    public void betweenToFileOutTest() throws IOException, InterruptedException {
        df.registerTempTable("history");
        //Dataset<Row> sqlDF = spark.sql("select * from history where timestamp between 1313331280 and 1313340309");
        Dataset<Row> sqlDF = spark.sql("select * from history").filter(col("timestamp").between(1313331280,1313340309));
        sqlDF.show();
        String path = "myfile.csv";
        String finalCsvFileName = "ohlc.csv";
        sqlDF.repartition(1).coalesce(1).write().format("csv").option("header", "true").mode("overwrite").save(path);

        //probably better way is : https://stackoverflow.com/questions/41990086/specifying-the-filename-when-saving-a-dataframe-as-a-csv

        File dir = new File(path);
        String[] extensions = new String[] { "csv" };
        logger.info("Getting all .csv files in {} including those in subdirectories" + dir.getCanonicalPath());
        List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
        if(files.size()!=1) throw new RuntimeException("Some old csv files exists !");
        for (File file : files) {
            logger.info("file: {}", file.getCanonicalPath());
        }
        files.get(0).renameTo(new File(finalCsvFileName));
    }
}
