package taskContents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

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

    public char getRole() {
        return role;
    }

    public int getDuration() {
        return duration;
    }

    public String getProtocol() {
        return protocol;
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
