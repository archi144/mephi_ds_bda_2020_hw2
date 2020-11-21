/*
package bd.homework2;


import bd.homework2.metrics.AggregatedMetrics;
import bd.homework2.metrics.RawMetrics;
import bd.homework2.utils.DateUtils;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.api.java.function.MapFunction;

import java.io.Serializable;

public class MetricAggregator implements Serializable {

    public static AggregatedMetrics aggregateMetrics(Dataset<RawMetrics> rawMetricsDataset, String scale) {
        Encoder<AggregatedMetrics> aggregatedMetricsEncoder = Encoders.bean(AggregatedMetrics.class);
        rawMetricsDataset
                .map((MapFunction<RawMetrics, AggregatedMetrics>) raw_metric -> {
                    raw_metric.settimestamp(DateUtils.round(raw_metric.gettimestamp(), scale));
                    return new AggregatedMetrics(raw_metric.getId(), raw_metric.gettimestamp().getTime(), scale, raw_metric.getValue());
                }, aggregatedMetricsEncoder);
    }

}
*/
