/**
 * Task.java
 * @description Classe que representa uma tarefa a ser executada por um NMS_Agent. A tarefa é composta por um
 * identificador, tipo, descrição, valor, timestamp, frequência e métricas.
 */

package message;

public class Task extends Data {
    private String frequency;
    private String[] metrics;

    public Task(String id, String type, String description, String value, String timestamp, String frequency, String[] metrics) {
        super(id, type, description, value, timestamp);
        this.frequency = frequency;
        this.metrics = metrics;
    }

    public String getFrequency() {
        return frequency;
    }

    public String[] getMetrics() {
        return metrics;
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
