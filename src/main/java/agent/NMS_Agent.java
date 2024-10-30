package agent;

import java.util.concurrent.*;
import message.*;

//public class NMS_Agent implements Runnable {
    // public static void main(String[] args) {
    //     try {
    //         // Registrar o agente no servidor e solicitar uma tarefa
    //         NetTaskClient netTaskClient = new NetTaskClient("localhost", 5000);
    //         netTaskClient.registerAgent();

    //         // Coletar e enviar métricas periodicamente
    //         MetricCollector metricCollector = new MetricCollector();
    //         while (true) {
    //             String metrics = metricCollector.collectMetrics();
    //             netTaskClient.sendMetrics(metrics);

    //             // Simular intervalo de coleta de métricas
    //             Thread.sleep(5000);
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    /*private String agentId;
    private MetricCollector metricCollector;
    private NetTaskClient netTaskClient;
    private AlertFlowClient alertFlowClient;

    public NMS_Agent(String agentId, String serverIp) {
        this.agentId = agentId;
        this.metricCollector = new MetricCollector();
        this.netTaskClient = new NetTaskClient(serverIp, 5000); // Porta UDP
        this.alertFlowClient = new AlertFlowClient(); // Porta TCP
    }

    @Override
    public void run() {
        System.out.println("Agente " + agentId + " iniciado.");
        startMetricCollection();
        startAlertMonitoring();
    }

    private void startMetricCollection() {
        new Thread(() -> {
            while (true) {
                try {
                    Data metricData = metricCollector.collectPing("8.8.8.8"); // Exemplo de recolha de ping
                    TaskResult result = new TaskResult(agentId, true, metricData.getValue().toString());
                    netTaskClient.sendTaskResult(result);
                    Thread.sleep(5000); // Recolha a cada 5 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startAlertMonitoring() {
        new Thread(() -> {
            while (true) {
                if (checkCriticalConditions()) {
                    Notification alert = new Notification(agentId, "Critical condition met!");
                    alertFlowClient.sendAlert(alert);
                }
                try {
                    Thread.sleep(10000); // Checa condições a cada 10 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}*/
