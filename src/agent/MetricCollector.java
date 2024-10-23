package agent;

public class MetricCollector {
    public String collectMetrics() {
        // Simular recolha de métricas
        double cpuUsage = Math.random() * 100;
        double ramUsage = Math.random() * 100;
        return String.format("{\"cpu_usage\": %.2f, \"ram_usage\": %.2f}", cpuUsage, ramUsage); // Formato da mensagem JSON (apenas um prototipo)
    }
}
