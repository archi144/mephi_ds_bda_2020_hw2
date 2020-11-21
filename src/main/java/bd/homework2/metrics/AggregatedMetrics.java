package bd.homework2.metrics;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class AggregatedMetrics implements Serializable {

    private Long id;
    private Long timestamp;
    private String scale;
    private Integer value;

    /**
     * Aggregated metric non arguments constructor
     * @return Aggregated metric
     */
    public AggregatedMetrics() {
    }

    /**
     * Aggregated metric all arguments constructor
     * @param id Id of metric group
     * @param timestamp timestamp of the metric
     * @param scale Scale of aggregation
     * @param value Value of the metric
     * @return Aggregated metric
     */
    public AggregatedMetrics(Long id, Long timestamp, String scale, Integer value) {
        this.id = id;
        this.timestamp = timestamp;
        this.scale = scale;
        this.value = value;
    }

    /**
     * Returns aggregated metric's group id
     * @return Aggregated metric's group id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set aggregated metric's group id
     * @param id New id of aggregated metric group
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns aggregated metric's timestamp
     * @return Aggregated metric's timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * Set aggregated metric's timestamp
     * @param timestamp New aggregated metric's timestamp
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns scale metric has been aggregated by
     * @return Aggregated metric's scale
     */
    public String getScale() {
        return scale;
    }

    /**
     * Set scale metric has been aggregated by
     * @param scale New aggregated metric's scale
     */
    public void setScale(String scale) {
        this.scale = scale;
    }

    /**
     * Returns aggregated metric's value
     * @return Aggregated metric's value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Set aggregated metric's value
     * @param value New aggregated metric's value
     */
    public void setValue(Integer value) {
        this.value = value;
    }

}
