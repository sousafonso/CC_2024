package storage;

import message.*;
import taskContents.*;

import java.util.*;

public class StorageModule {
    public static class MetricStats {
        private double lastValue;
        private double minValue = Double.MAX_VALUE;
        private double maxValue = Double.MIN_VALUE;

        public void update(double value) {
            lastValue = value;
            minValue = Math.min(minValue, value);
            maxValue = Math.max(maxValue, value);
        }

        public double getLastValue() {
            return lastValue;
        }

        public double getMinValue() {
            return minValue;
        }

        public double getMaxValue() {
            return maxValue;
        }
    }

    private final Map<String, Map<MetricName, MetricStats>> metricsStorage = new HashMap<>();
    private final Map<String, List<Notification>> alertsStorage = new HashMap<>();

    public synchronized void storeMetric(String deviceId, MetricName metricName, double value) {
        metricsStorage
                .computeIfAbsent(deviceId, k -> new HashMap<>())
                .computeIfAbsent(metricName, k -> new MetricStats())
                .update(value);
    }

    public synchronized void storeAlert(String deviceId, Notification alert) {
        alertsStorage.computeIfAbsent(deviceId, k -> new ArrayList<>()).add(alert);
    }

    public synchronized Map<MetricName, MetricStats> getMetrics(String deviceId) {
        return metricsStorage.getOrDefault(deviceId, Collections.emptyMap());
    }

    public synchronized List<Notification> getAlerts(String deviceId) {
        return alertsStorage.getOrDefault(deviceId, Collections.emptyList());
    }

    public synchronized void displayAllMetrics() {
        System.out.println("=== Métricas ===");
        metricsStorage.forEach((deviceId, metrics) -> {
            System.out.println("Dispositivo: " + deviceId);
            metrics.forEach((metricName, stats) -> {
                System.out.printf("%s - Último Valor: %.2f, Mínimo: %.2f, Máximo: %.2f%n",
                        metricName, stats.getLastValue(), stats.getMinValue(), stats.getMaxValue());
            });
        });
    }

    public synchronized void displayAllAlerts() {
        System.out.println("=== Alertas ===");
        alertsStorage.forEach((deviceId, alerts) -> {
            System.out.println("Dispositivo: " + deviceId);
            alerts.forEach(alert -> {
                System.out.printf("Métrica: %s, Valor: %.2f, Timestamp: %s%n",
                        alert.getMetricName(), alert.getMeasurement(), alert.getTimestamp());
            });
        });
    }
}