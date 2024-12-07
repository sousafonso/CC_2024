package message;

import taskContents.MetricName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskResult extends Data {
    private String taskId;
    private MetricName metricName;
    private double result;
    private LocalDateTime timestamp;
    private String measureInterface;

    // Construtor
    public TaskResult(String taskId, MetricName metricName, double result, LocalDateTime timestamp, String measureInterface) {
        this.taskId = taskId;
        this.metricName = metricName;
        this.result = result;
        this.timestamp = timestamp;
        this.measureInterface = measureInterface;
    }

    public TaskResult(String[] fields) {
        int startIndex = 0;
        this.taskId = fields[startIndex++];
        this.metricName = MetricName.fromInteger(Integer.parseInt(fields[startIndex++]));
        this.result = Double.parseDouble(fields[startIndex++]);
        this.timestamp = LocalDateTime.parse(fields[startIndex++]);
        this.measureInterface = fields[startIndex];
    }

    // Getters e Setters
    public String getTaskId() {
        return taskId;
    }

    public MetricName getMetricName() {
        return metricName;
    }

    public Double getResult() {
        return result;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMeasureInterface() {
        return measureInterface;
    }

    @Override
    public String getPayload() {
        return taskId + ";" +
                metricName.toInteger() + ";"
                + result + ";"
                + timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}

        if (obj == null || obj.getClass() != this.getClass()) {return false;}

        TaskResult that = (TaskResult) obj;
        return this.taskId.equals(that.taskId) && this.metricName == that.metricName && this.result == that.result && this.timestamp.equals(that.timestamp);
    }
}
