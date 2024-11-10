package message;

import taskContents.LinkMetric;
import taskContents.LocalMetric;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

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

    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeChars(super.getTimestamp());

            dos.flush();
        } catch (IOException e) {
            System.out.println("Erro ao serializar objeto");
        }

        return out.toByteArray();
    }
}
