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
    private List<Measure> measures = new ArrayList<>();

    public MetricStats() {}

    public synchronized void update(String agentId, double value, LocalDateTime timestamp) {
        minValue = Math.min(minValue, value);
        maxValue = Math.max(maxValue, value);
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

    @Override
    public String toString() {
        return "Valor máximo: " + getMaxValue() + "\n" +
                "Valor mínimo: " + getMinValue() + "\n" +
                "Valor médio: " + getAverage();
    }

    public String toStringMeasures(){
        StringBuilder sb = new StringBuilder();
        sb.append(this);
        sb.append("Últimas medições feitas:");
        for (Measure measure : measures) {
            sb.append("\n").append(measure.toString());
        }

        return sb.toString();
    }
}
