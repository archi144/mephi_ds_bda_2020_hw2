package bd.homework2.utils;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.spark.connector.cql.CassandraConnector;
import org.apache.spark.SparkContext;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CassandraUtils {

    /**
     * Initialize session in Cassandra
     *
     * @param sparkContext Spark Context
     */
    public static void init(SparkContext sparkContext) {
        CassandraConnector connector = CassandraConnector.apply(sparkContext.getConf());
        CqlSession session = connector.openSession();
        session.execute("CREATE TABLE IF NOT EXISTS app_metrics.aggregated_metrics (id int, timestamp timestamp, scale varchar, value int, " +
                "PRIMARY KEY ((id), timestamp, scale))");
    }



}