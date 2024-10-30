package agent;

public class NMS_Agent {
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

    private String agentId;
    private MetricCollector metricCollector;
    private NetTaskClient netTaskClient;
    private AlertFlowClient alertFlowClient;

    public NMS_Agent(String agentId) {
        this.agentId = agentId;
        this.metricCollector = new MetricCollector();
        this.netTaskClient = new NetTaskClient();
        this.alertFlowClient = new AlertFlowClient();
    }

    public void initialize() {
        try {
            // Registrar o agente no servidor e solicitar uma tarefa
            netTaskClient.registerAgent();

            // Coletar e enviar métricas periodicamente
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
