package storage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MetricStats {
    private final int maxMeasures = 10;
    private double minValue = Double.MAX_VALUE;
    private double maxValue = Double.MIN_VALUE;
    private double count = 0;
    private double sum = 0;
    private String minAgent = "";
    private String maxAgent = "";
    private String minInterface = ""; //usada para saber de que interface foi medida a métrica no caso de INTERFACE_STATS
    private String maxInterface = ""; //usada para saber de que interface foi medida a métrica no caso de INTERFACE_STATS
    private List<Measure> measures = new ArrayList<>();

    public MetricStats() {}

    public synchronized void update(String agentId, double value, LocalDateTime timestamp, String measureInterface) {
        if(value < minValue) {
            minValue = value;
            minAgent = agentId;
            minInterface = measureInterface;
        }
        if(value > maxValue) {
            maxValue = value;
            maxAgent = agentId;
            maxInterface = measureInterface;
        }
        count++;
        sum += value;

        if(measures.size() >= maxMeasures) {
            measures.removeFirst();
        }
        measures.add(new Measure(value, timestamp, measureInterface));
    }

    public synchronized double getMinValue() {
        return minValue;
    }

    public synchronized double getMaxValue() {
        return maxValue;
    }

    public synchronized double getAverage() {
        return sum / count;
    }

    public synchronized String getMinAgent() {
        return minAgent;
    }

    public synchronized String getMaxAgent() {
        return maxAgent;
    }

    public String toStringStats(boolean global) {
        StringBuilder sb = new StringBuilder();
        sb.append("Valor máximo");
        if(global) {
            sb.append(" por ").append(getMaxAgent());
            if(!maxInterface.isEmpty()) {
                sb.append(" (").append(maxInterface).append(")");
            }
        }
        sb.append(": ").append(getMaxValue()).append('\n');
        sb.append("Valor mínimo");
        if(global) {
            sb.append(" por ").append(getMinAgent());
            if(!minInterface.isEmpty()) {
                sb.append(" (").append(minInterface).append(")");
            }
        }
        sb.append(": ").append(getMinValue()).append('\n');
        sb.append("Valor médio: ").append(getAverage());

        return sb.toString();
    }

    public String toStringMeasures(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.toStringStats(false));
        sb.append("Últimas medições feitas:");
        for (Measure measure : measures) {
            sb.append("\n").append(measure.toString());
        }

        return sb.toString();
    }
}
