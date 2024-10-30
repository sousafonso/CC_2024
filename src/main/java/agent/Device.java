package agent;

public class Device {
    private String deviceId;
    private DeviceMetrics deviceMetrics;
    private LinkMetrics linkMetrics;
    //private AlertFlowConditions alertFlowConditions;

    // Getters e Setters
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceMetrics getDeviceMetrics() {
        return deviceMetrics;
    }

    public void setDeviceMetrics(DeviceMetrics deviceMetrics) {
        this.deviceMetrics = deviceMetrics;
    }

    public LinkMetrics getLinkMetrics() {
        return linkMetrics;
    }

    public void setLinkMetrics(LinkMetrics linkMetrics) {
        this.linkMetrics = linkMetrics;
    }

    /*public AlertFlowConditions getAlertFlowConditions() {
        return alertFlowConditions;
    }

    public void setAlertFlowConditions(AlertFlowConditions alertFlowConditions) {
        this.alertFlowConditions = alertFlowConditions;
    }*/
}