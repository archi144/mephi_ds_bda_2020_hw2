package bd.homework2;

import bd.homework2.metrics.AggregatedMetrics;
import bd.homework2.utils.CassandraUtils;
import bd.homework2.utils.DateUtils;
import com.datastax.oss.driver.api.querybuilder.Raw;
import lombok.extern.log4j.Log4j;
import bd.homework2.metrics.RawMetrics;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.spark.connector.cql.CassandraConnector;
import com.datastax.spark.connector.japi.CassandraJavaUtil;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import sun.util.calendar.BaseCalendar;

import static org.apache.spark.sql.functions.col;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Collections;
import java.io.Serializable;

@Log4j
public class SparkSQLApp {

    public static void main(String[] args) throws Exception {

        SparkConf conf = new SparkConf()
                .setAppName("SparkSQLApp")
                .setMaster("local[*]")
                .set("spark.cassandra.connection.host", "127.0.0.1");
        SparkContext sparkContext = new SparkContext(conf);
        SparkSession sparkSession = new SparkSession(sparkContext)
                .builder()
                .getOrCreate();

        CassandraUtils.init(sparkContext);

        JavaRDD javaRDD = CassandraJavaUtil.javaFunctions(sparkContext)
                .cassandraTable("app_metrics", "raw_metrics", CassandraJavaUtil.mapRowTo(RawMetrics.class))
                .select("id", "timestamp", "value");
        Encoder<RawMetrics> rawMetricsEncoder = Encoders.bean(RawMetrics.class);
        Dataset<RawMetrics> rawMetricsDataset = sparkSession.createDataset(javaRDD.rdd(),rawMetricsEncoder);
        String scale = args[0];
        Encoder<AggregatedMetrics> aggregatedMetricsEncoder = Encoders.bean(AggregatedMetrics.class);
        Dataset<AggregatedMetrics> aggregatedMetricsDataset = rawMetricsDataset
                .map((MapFunction<RawMetrics, AggregatedMetrics>) raw_metric -> {
                            raw_metric.settimestamp(DateUtils.round(raw_metric.gettimestamp(), scale));
                            return new AggregatedMetrics(raw_metric.getId(), raw_metric.gettimestamp().getTime(), scale, raw_metric.getValue());
                        }, aggregatedMetricsEncoder);
        aggregatedMetricsDataset.createOrReplaceTempView("table");
        aggregatedMetricsDataset.sqlContext().sql("SELECT id, timestamp, scale, AVG(value) as value FROM table GROUP BY id, timestamp, scale")
                .write()
                .mode("append")
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", "app_metrics")
                .option("table", "aggregated_metrics")
                .save();

  }
}
