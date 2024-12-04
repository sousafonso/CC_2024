package message;

import taskContents.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification extends Data {
    private String taskID;
    private MetricName metricName;
    private double measurement;
    private LocalDateTime timestamp;

    public Notification(String taskID, MetricName metricName, double measurement, LocalDateTime timestamp) {
        this.taskID = taskID;
        this.metricName = metricName;
        this.measurement = measurement;
        this.timestamp = timestamp;
    }

    public Notification(String[] fields){
        int startIndex = 0;
        this.taskID = fields[startIndex++];
        this.metricName = MetricName.fromInteger(Integer.parseInt(fields[startIndex++]));
        this.measurement = Double.parseDouble(fields[startIndex++]);
        this.timestamp = LocalDateTime.parse(fields[startIndex]);
    }

    public String getTaskID() {
        return taskID;
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
        return taskID + ";" + metricName.toInteger() + ";" + measurement + ";" + timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
