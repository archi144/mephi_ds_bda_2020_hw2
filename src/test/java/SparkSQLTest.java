
import bd.homework2.metrics.AggregatedMetrics;
import bd.homework2.metrics.RawMetrics;
import bd.homework2.utils.DateUtils;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.apache.spark.sql.functions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SparkDatasetTest {

    private SparkContext sparkContext;
    private SparkSession sparkSession;

    private final List<RawMetrics> input = new ArrayList<>();
    private final List<AggregatedMetrics> expectedOutput = new ArrayList<>();

    @BeforeEach
    public void init() throws ParseException {
        SparkConf conf = new SparkConf();
        conf.setMaster("local[*]");
        conf.setAppName("Spark");
        sparkContext = new SparkContext(conf);
        sparkSession = new SparkSession(sparkContext);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        input.add(new RawMetrics(1L, dateFormat.parse("2020-11-12 12:09:45"), 50));
        input.add(new RawMetrics(1L, dateFormat.parse("2020-11-12 17:09:55"), 60));
        input.add(new RawMetrics(1L, dateFormat.parse("2020-11-12 17:10:05"), 40));
        input.add(new RawMetrics(2L, dateFormat.parse("2020-11-12 17:13:05"), 30));
        input.add(new RawMetrics(2L, dateFormat.parse("2020-11-12 17:13:15"), 70));
        input.add(new RawMetrics(2L, dateFormat.parse("2020-11-12 17:13:25"), 20));

        expectedOutput.add(new AggregatedMetrics(1L, dateFormat.parse("2020-11-12 17:09:00").getTime(), "1m", 55));
        expectedOutput.add(new AggregatedMetrics(1L, dateFormat.parse("2020-11-12 17:10:00").getTime(), "1m", 40));
        expectedOutput.add(new AggregatedMetrics(2L, dateFormat.parse("2020-11-12 17:13:00").getTime(), "1m", 40));
    }

    @Test
    public void testMetricAggregation() throws InterruptedException {
        JavaRDD<RawMetrics> rawMetricsJavaRDD = CassandraJavaUtil.javaFunctions(sparkContext)
                .cassandraTable("app_metrics", "raw_metrics", CassandraJavaUtil.mapRowTo(RawMetrics.class))
                .select("id", "timestamp", "value");
        Encoder<RawMetrics> rawMetricsEncoder = Encoders.bean(RawMetrics.class);
        Dataset<RawMetrics> rawMetricsDataset = sparkSession.createDataset(rawMetricsJavaRDD.rdd(),rawMetricsEncoder);
        Encoder<AggregatedMetrics> aggregatedMetricsEncoder = Encoders.bean(AggregatedMetrics.class);
        Dataset<AggregatedMetrics> aggregatedMetricsDataset = rawMetricsDataset
                .map((MapFunction<RawMetrics, AggregatedMetrics>) raw_metric -> {
                    raw_metric.settimestamp(DateUtils.round(raw_metric.gettimestamp(), "1m"));
                    return new AggregatedMetrics(raw_metric.getId(), raw_metric.gettimestamp().getTime(), "1m", raw_metric.getValue());
                }, aggregatedMetricsEncoder);
        List<AggregatedMetrics> resultList = aggregatedMetricsDataset.
                groupBy("id","timestamp","scale")
                .agg(expr("avg(value) as value"))
                .as(aggregatedMetricsEncoder).collectAsList();
        assertEquals(expectedOutput.size(), resultList.size());
        assertTrue(true);
    }
}




