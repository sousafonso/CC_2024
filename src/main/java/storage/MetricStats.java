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
    private List<Measure> measures = new ArrayList<>();

    public MetricStats() {}

    public synchronized void update(String agentId, double value, LocalDateTime timestamp) {
        if(value < minValue) {
            minValue = value;
            minAgent = agentId;
        }
        if(value > maxValue) {
            maxValue = value;
            maxAgent = agentId;
        }
        count++;
        sum += value;

        if(measures.size() >= maxMeasures) {
            measures.removeFirst();
        }
        measures.add(new Measure(agentId, value, timestamp));
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
        }
        sb.append(": ").append(getMaxValue()).append('\n');
        sb.append("Valor mínimo");
        if(global) {
            sb.append(" por ").append(getMinAgent());
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
