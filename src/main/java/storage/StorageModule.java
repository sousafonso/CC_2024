package storage;

import message.*;
import taskContents.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StorageModule {
    private ConcurrentHashMap<String, ConcurrentHashMap<MetricName, MetricStats>> agentMetricsStorage = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, List<Notification>> agentAlertsStorage = new ConcurrentHashMap<>();

    private ConcurrentHashMap<MetricName, MetricStats> globalStatsStorage = new ConcurrentHashMap<>();
    private List<Notification> globalAlertsStorage = new ArrayList<>();

    public synchronized void storeMetric(String deviceId, MetricName metricName, double value, LocalDateTime timestamp, String measureInterface) {
        agentMetricsStorage
                .computeIfAbsent(deviceId, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(metricName, k -> new MetricStats())
                .update(deviceId, value, timestamp, measureInterface);

        globalStatsStorage
                .computeIfAbsent(metricName, k -> new MetricStats())
                .update(deviceId, value, timestamp, measureInterface);
    }

    public synchronized void storeAlert(String deviceId, Notification alert) {
        final int maxStoredAlerts = 10;
        List<Notification> measureList = agentAlertsStorage.get(deviceId);
        if (measureList == null) {
            measureList = new ArrayList<>();
        }
        else if (measureList.size() >= maxStoredAlerts) {
            measureList.removeFirst();
        }
        measureList.add(new Notification(alert));
        agentAlertsStorage.put(deviceId, measureList);

        if (globalAlertsStorage.size() >= maxStoredAlerts) {
            globalAlertsStorage.removeFirst();
        }

        globalAlertsStorage.add(new Notification(deviceId, alert.getMetricName(), alert.getMeasurement(), alert.getTimestamp()));
    }

    public synchronized Set<Map.Entry<MetricName, MetricStats>> getAllMetrics(String agentId) {
        Map<MetricName, MetricStats> metricsForAgent = agentMetricsStorage.get(agentId);
        return metricsForAgent != null ? metricsForAgent.entrySet() : Collections.emptySet();
    }

    public synchronized MetricStats getMetrics(String agentId, MetricName metricName, boolean global) {
        if(global){
            return globalStatsStorage.get(metricName);
        } else {
            Map<MetricName, MetricStats> metricsForAgent = agentMetricsStorage.get(agentId);
            return metricsForAgent != null ? metricsForAgent.get(metricName) : null;
        }
    }

    public synchronized List<Notification> getAlerts(String deviceId, boolean global) {
        if(global){
            return globalAlertsStorage;
        } else {
            List<Notification> alertsForAgent = agentAlertsStorage.get(deviceId);
            return alertsForAgent != null ? agentAlertsStorage.get(deviceId) : new ArrayList<>();
        }
    }
}