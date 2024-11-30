package taskContents;

import java.util.List;

public class LocalMetric {
    private MetricName metricName;
    private int numInterfaces;
    private List<String> interfaces;

    public LocalMetric(MetricName metricName, List<String> interfaces) {
        this.metricName = metricName;
        this.numInterfaces = interfaces == null ? 0 : interfaces.size();
        this.interfaces = interfaces;
    }

    public MetricName getMetricName() {
        return metricName;
    }

    public int getNumInterfaces() {
        return numInterfaces;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public String getPayload() {
        StringBuilder payload = new StringBuilder();
        payload.append(metricName.toInteger()).append(";").append(numInterfaces);
        for (String iface : interfaces) {
            payload.append(";").append(iface);
        }
        return payload.toString();
    }

    @Override
    public String toString() {
        return "name= " + metricName.name() + ", numInterfaces= " + numInterfaces + ", interfaces= " + interfaces;
    }
}
