package message;

import taskContents.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification extends Data {
    private String ID; //taskID, mas pode funcionar como agentID para efeitos de apresentação no servidor
    private MetricName metricName;
    private double measurement;
    private LocalDateTime timestamp;

    public Notification(String ID, MetricName metricName, double measurement, LocalDateTime timestamp) {
        this.ID = ID;
        this.metricName = metricName;
        this.measurement = measurement;
        this.timestamp = timestamp;
    }

    public Notification(String[] fields){
        int startIndex = 0;
        this.ID = fields[startIndex++];
        this.metricName = MetricName.fromInteger(Integer.parseInt(fields[startIndex++]));
        this.measurement = Double.parseDouble(fields[startIndex++]);
        this.timestamp = LocalDateTime.parse(fields[startIndex]);
    }

    public Notification(Notification notification) {
        this.ID = notification.ID;
        this.metricName = notification.metricName;
        this.measurement = notification.measurement;
        this.timestamp = notification.timestamp;
    }

    public String getID() {
        return ID;
    }

    public MetricName getMetricName() {
        return metricName;
    }

    public double getMeasurement() {
        return measurement;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String getPayload() {
        return ID + ";" + metricName.toInteger() + ";" + measurement + ";" + timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(this.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("] ");
        sb.append(this.metricName).append(" -> ").append(this.measurement);
        switch(metricName){
            case CPU_USAGE, RAM_USAGE, PACKET_LOSS -> sb.append(" %");
            case INTERFACE_STATS -> sb.append(" packets");
            case BANDWIDTH -> sb.append(" Mbits/s");
            case JITTER, LATENCY -> sb.append(" ms");
        }

        return sb.toString();
    }

}
