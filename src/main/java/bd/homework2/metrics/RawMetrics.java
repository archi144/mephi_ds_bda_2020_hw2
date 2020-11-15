package bd.homework2.metrics;

import java.io.Serializable;
import java.util.Date;

public class RawMetrics implements Serializable {

    private Long id;
    private Date timestamp;
    private Integer value;

    /**
     * Metric non arguments constructor
     * @return Metric
     */
    public RawMetrics() {
    }

    /**
     * Metric all arguments constructor
     * @param id Id of metric group
     * @param timestamp timestamp of the metric
     * @param value Value of the metric
     * @return Metric
     */
    public RawMetrics(Long id, Date timestamp, Integer value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    /**
     * Returns metric's group id
     * @return Metric's group id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set metric's group id
     * @param id New id of metric group
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns metric's timestamp
     * @return Metric's timestamp
     */
    public Date gettimestamp() {
        return timestamp;
    }

    /**
     * Set metric's timestamp
     * @param timestamp New metric's timestamp
     */
    public void settimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns metric's value
     * @return Metric's value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Set metric's value
     * @param value New metric's value
     */
    public void setValue(Integer value) {
        this.value = value;
    }
}









































































