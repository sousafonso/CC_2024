/**
 * Task.java
 * @description Classe que representa uma tarefa a ser executada por um NMS_Agent. A tarefa é composta por um
 * identificador, tipo, descrição, valor, timestamp, frequência e métricas.
 */

package message;

import java.util.List;

import agent.Device;

public class Task extends Data {
    private String id; 
    private String frequency;
    private String[] metrics;
    private List<Device> devices;

    public Task(String DataID, String type, String description, String value, String timestamp, String frequency, String[] metrics, List<Device> devices, String TaskID) {
        super(DataID, type, description, value, timestamp);
        this.frequency = frequency;
        this.metrics = metrics;
        this.devices = devices;
        this.id = TaskID;
    }

    public String getFrequency() {
        return frequency;
    }

    public String[] getMetrics() {
        return metrics;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setMetrics(String[] metrics) {
        this.metrics = metrics;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", value='" + getValue() + '\'' +
                ", timestamp='" + getTimestamp() + '\'' +
                ", frequency='" + frequency + '\'' +
                ", metrics=" + metrics +
                '}';
    }
}