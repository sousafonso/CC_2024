package taskContents;

public abstract class LinkMetric {
    private MetricName metricName;
    private String destination;

    public LinkMetric(MetricName metricName, String destination) {
        this.metricName = metricName;
        this.destination = destination;
    }

    public MetricName getMetricName() {
        return metricName;
    }

    public String getDestination() {
        return destination;
    }

    public abstract String getPayload();
}
