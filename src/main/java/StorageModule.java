import java.util.HashMap;
import java.util.Map;

import message.Notification;
import message.TaskResult;

public class StorageModule {
    private Map<String, TaskResult> taskResults = new HashMap<>();
    private Map<String, Notification> alerts = new HashMap<>();

    // Armazena métricas
    public synchronized void storeTaskResult(String taskId, TaskResult result) {
        taskResults.put(taskId, result);
        System.out.println("Métrica armazenada para Task ID: " + taskId);
    }

    // Armazena alertas
    public synchronized void storeAlert(String alertId, Notification alert) {
        alerts.put(alertId, alert);
        System.out.println("Alerta armazenado: " + alertId);
    }

    // Exibe todas as métricas e alertas
    public void displayMetrics() {
        System.out.println("----- Métricas -----");
        taskResults.forEach((k, v) -> System.out.println(k + ": " + v.getResultData()));
        System.out.println("----- Alertas -----");
        //alerts.forEach((k, v) -> System.out.println(k + ": " + v.getMessage()));
    }
}