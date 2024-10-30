package message;

public class TaskResult extends Data {
    private String taskId;
    private boolean success;
    private String resultData;

    // Construtor
    public TaskResult(String taskId, boolean success, String resultData, String DataID, String type, String description, String value, String timestamp) {
        super(DataID, type, description, value, timestamp);
        this.taskId = taskId;
        this.success = success;
        this.resultData = resultData;
    }

    // Getters e Setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }
}
