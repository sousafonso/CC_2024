package taskContents;

public class IperfMetric extends LinkMetric{
    private char role;
    private int duration;
    private String protocol;

    public IperfMetric(MetricName name, String destination, char role, int duration, String protocol) {
        super(name, destination);
        this.role = role;
        this.duration = duration;
        this.protocol = protocol;
    }

    public String getPayload() {
        return super.getMetricName().toInteger() + ";" +
                super.getDestination() + ";" +
                role + ";" +
                duration  + ";" +
                protocol;
    }

    public String toString() {
        return "name= " + super.getMetricName().name() + ", destination" + super.getDestination() + ", role= " + role + ", duration= " + duration + ", protocol= " + protocol;
    }
}
