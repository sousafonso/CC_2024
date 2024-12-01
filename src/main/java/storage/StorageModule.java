package storage; /**
 * StorageModule.java
 * @description: Módulo que armazena métricas e alertas de forma centralizada no servidor. Usado por NetTaskHandler e AlertFlowHandler para armazenar dados.
    Interage com NMS_Server para exibir métricas e alertas periodicamente.
 * 
 * @responsibility:
 *  Armazenar resultados de tarefas (métricas) e alertas críticos recebidos dos agentes.
    Exibir periodicamente o conteúdo armazenado.
 */

import java.util.HashMap;
import java.util.Map;

import message.Notification;
import message.TaskResult;

public class StorageModule {
    private Map<String, Map<String, TaskResult>> clientTaskResults = new HashMap<>();
    private Map<String, Map<String, Notification>> clientAlerts = new HashMap<>();

    // Armazena métricas
    public synchronized void storeTaskResult(String clientId, String taskId, TaskResult result) {
        clientTaskResults.computeIfAbsent(clientId, k -> new HashMap<>()).put(taskId, result);
        System.out.println("Métrica armazenada para Task ID: " + taskId + " do cliente: " + clientId);
    }

    // Armazena alertas
    public synchronized void storeAlert(String clientId, String alertId, Notification alert) {
        clientAlerts.computeIfAbsent(clientId, k -> new HashMap<>()).put(alertId, alert);
        System.out.println("Alerta armazenado: " + alertId + " do cliente: " + clientId);
    }

    // Exibe todas as métricas e alertas de um cliente
    public void displayMetricsAndAlerts(String clientId) {
        System.out.println("----- Métricas para o cliente: " + clientId + " -----");
        clientTaskResults.getOrDefault(clientId, new HashMap<>()).forEach((taskId, taskResult) -> 
            System.out.println("Task ID: " + taskId + ", Result: " + taskResult.getResult()));

        System.out.println("----- Alertas para o cliente: " + clientId + " -----");
        clientAlerts.getOrDefault(clientId, new HashMap<>()).forEach((alertId, alert) -> 
            System.out.println("Alert ID: " + alertId + ", Message: " + alert.getMeasurement()));
    }
}