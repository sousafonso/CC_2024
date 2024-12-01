package message;

import taskContents.MetricName;

public class TaskResult extends Data {
    private String taskId;
    private MetricName metricName;
    private double result;

    // Construtor
    public TaskResult(String taskId, MetricName metricName, double result) {
        //super(timestamp);
        this.taskId = taskId;
        this.metricName = metricName;
        this.result = result;
    }

    public TaskResult(String[] fields) {
        int startIndex = 0;
        this.taskId = fields[startIndex++];
        this.metricName = MetricName.fromInteger(Integer.parseInt(fields[startIndex++]));
        this.result = Double.parseDouble(fields[startIndex]);
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

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setMetricName(MetricName metricName) {
        this.metricName = metricName;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    @Override
    public String getPayload() {
        String s = taskId + ";" +
                metricName.toInteger() + ";"
                + result;

        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}

        if (obj == null || obj.getClass() != this.getClass()) {return false;}

        TaskResult that = (TaskResult) obj;
        return this.taskId.equals(that.taskId) && this.metricName == that.metricName && this.result == that.result;
    }
}
