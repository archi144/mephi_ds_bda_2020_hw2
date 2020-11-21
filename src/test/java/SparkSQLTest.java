
import bd.homework2.metrics.AggregatedMetrics;
import bd.homework2.metrics.RawMetrics;
import bd.homework2.utils.DateUtils;
import com.datastax.driver.core.ParseUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.spark.sql.functions.*;
import static org.junit.jupiter.api.Assertions.*;

class SparkDatasetTest {

    private SparkContext sparkContext;
    private SparkSession sparkSession;

    private final List<RawMetrics> input = new ArrayList<>();
    private final List<AggregatedMetrics> expectedOutput = new ArrayList<>();

    @BeforeEach
    public void init() throws ParseException {
        SparkConf conf = new SparkConf();
        conf.setMaster("local[*]");
        conf.setAppName("SparkSQLApp Unit tests");
        sparkContext = new SparkContext(conf);
        sparkSession = new SparkSession(sparkContext);
        input.add(new RawMetrics(1L, ParseUtils.parseDate("2020-11-12 17:09:45"), 50));
        input.add(new RawMetrics(1L, ParseUtils.parseDate("2020-11-12 17:09:55"), 60));
        input.add(new RawMetrics(1L, ParseUtils.parseDate("2020-11-12 17:10:05"), 40));
        input.add(new RawMetrics(2L, ParseUtils.parseDate("2020-11-12 17:13:05"), 30));
        input.add(new RawMetrics(2L, ParseUtils.parseDate("2020-11-12 17:13:15"), 70));
        input.add(new RawMetrics(2L, ParseUtils.parseDate("2020-11-12 17:13:25"), 20));

        expectedOutput.add(new AggregatedMetrics(1L, ParseUtils.parseDate("2020-11-12 17:09:00").getTime(), "1m", 55));
        expectedOutput.add(new AggregatedMetrics(1L, ParseUtils.parseDate("2020-11-12 17:10:00").getTime(), "1m", 40));
        expectedOutput.add(new AggregatedMetrics(2L, ParseUtils.parseDate("2020-11-12 17:13:00").getTime(), "1m", 40));
    }

    @Test
    public void testMetricAggregation() throws InterruptedException {
        JavaRDD<RawMetrics> javaRDD = JavaSparkContext.fromSparkContext(sparkContext).parallelize(input);
        Encoder<RawMetrics> rawMetricsEncoder = Encoders.bean(RawMetrics.class);
        Dataset<RawMetrics> rawMetricsDataset = sparkSession.createDataset(javaRDD.rdd(),rawMetricsEncoder);
        rawMetricsDataset.select("timestamp").show();
        rawMetricsDataset.printSchema();
        Encoder<AggregatedMetrics> aggregatedMetricsEncoder = Encoders.bean(AggregatedMetrics.class);

        Dataset<AggregatedMetrics> aggregatedMetricsDataset = rawMetricsDataset
                .map((MapFunction<RawMetrics, AggregatedMetrics>) raw_metric -> {
                    raw_metric.settimestamp(DateUtils.round(raw_metric.gettimestamp(), "1m"));
                    return new AggregatedMetrics(raw_metric.getId(), raw_metric.gettimestamp().getTime(), "1m", raw_metric.getValue());
                }, aggregatedMetricsEncoder);

        aggregatedMetricsDataset.show();

        List<AggregatedMetrics> resultList = aggregatedMetricsDataset.
                groupBy("id","timestamp","scale")
                .agg(expr("avg(value)").cast("int").as("value")).sort("id")
                .as(aggregatedMetricsEncoder).collectAsList();
        aggregatedMetricsDataset.
                groupBy("id","timestamp","scale")
                .agg(expr("avg(value)").cast("int").as("value")).sort("id").show();
        assertEquals(expectedOutput.size(), resultList.size());
        resultList.forEach(x->System.out.println(x.getId() + "," + x.getTimestamp() + "," + x.getScale() + "," + x.getValue()));
        expectedOutput.forEach(x->System.out.println(x.getId() + "," + x.getTimestamp() + "," + x.getScale() + "," + x.getValue()));
        System.out.println(expectedOutput.get(0).getTimestamp() + " " + resultList.get(0).getTimestamp());
        for (int i = 0; i < expectedOutput.size(); i++)
        {
            assertTrue(expectedOutput.get(i).getId().equals(resultList.get(i).getId()));
            assertTrue(expectedOutput.get(i).getTimestamp().equals(resultList.get(i).getTimestamp()) );
            assertTrue(expectedOutput.get(i).getScale().equals(resultList.get(i).getScale()));
            assertTrue(expectedOutput.get(i).getValue().equals(resultList.get(i).getValue()));
        }
    }
    @AfterEach
    public  void finish() {
        sparkSession.stop();
        sparkContext.stop();
    }
}




