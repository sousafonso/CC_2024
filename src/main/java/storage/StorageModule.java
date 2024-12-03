package storage;
import java.util.*;
import message.*;

public class StorageModule {
    private final Map<String, List<TaskResult>> taskResults = new HashMap<>();
    private final Map<String, List<Notification>> alerts = new HashMap<>();

    // Armazenar as métricas coletadas
    public synchronized void storeTaskResult(String deviceId, TaskResult result) {
        taskResults.putIfAbsent(deviceId, new ArrayList<>());
        taskResults.get(deviceId).add(result);
    }

    // Armazenar alertas
    public synchronized void storeAlert(String deviceId, Notification alert) {
        alerts.putIfAbsent(deviceId, new ArrayList<>());
        alerts.get(deviceId).add(alert);
    }

    // Exibir todas as métricas armazenadas
    public synchronized void displayAllMetrics() {
        System.out.println("=== Métricas ===");
        taskResults.forEach((deviceId, results) -> {
            System.out.println("Dispositivo: " + deviceId);
            results.forEach(result -> System.out.println(result));
        });
    }

    // Exibir todos os alertas armazenados
    public synchronized void displayAllAlerts() {
        System.out.println("=== Alertas ===");
        alerts.forEach((deviceId, alertsList) -> {
            System.out.println("Dispositivo: " + deviceId);
            alertsList.forEach(alert -> System.out.println(alert));
        });
    }

}
