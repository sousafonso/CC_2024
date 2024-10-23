package agent;

public class NMS_Agent {
    public static void main(String[] args) {
        try {
            // Registrar o agente no servidor e solicitar uma tarefa
            NetTaskClient netTaskClient = new NetTaskClient("localhost", 5000);
            netTaskClient.registerAgent();

            // Coletar e enviar métricas periodicamente
            MetricCollector metricCollector = new MetricCollector();
            while (true) {
                String metrics = metricCollector.collectMetrics();
                netTaskClient.sendMetrics(metrics);

                // Simular intervalo de coleta de métricas
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
