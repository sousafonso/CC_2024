package message;

import taskContents.MetricName;

public class TaskResult extends Data {
    private String taskId;
    private MetricName metricName;
    private String result;

    // Construtor
    public TaskResult(String taskId, MetricName metricName, String result) {
        //super(timestamp);
        this.taskId = taskId;
        this.metricName = metricName;
        this.result = result;
    }

    public TaskResult(String[] fields, int startIndex) {
        this.taskId = fields[startIndex++];
        this.metricName = MetricName.fromInteger(Integer.parseInt(fields[startIndex++]));
        this.result = fields[startIndex];
    }

    // Getters e Setters
    public String getTaskId() {
        return taskId;
    }

    public MetricName getMetricName() {
        return metricName;
    }

    public String getResult() {
        return result;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setMetricName(MetricName metricName) {
        this.metricName = metricName;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String getPayload() {
        String s = taskId + ";" +
                metricName.toInteger() + ";"
                + result;

        return s;
    }
}
