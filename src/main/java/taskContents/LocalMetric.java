package taskContents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public class LocalMetric {
    private MetricName metricName;
    private int numInterfaces;
    private List<String> interfaces;

    public LocalMetric(MetricName metricName, List<String> interfaces) {
        this.metricName = metricName;
        this.numInterfaces = interfaces == null ? 0 : interfaces.size();
        this.interfaces = interfaces;
    }

    public MetricName getMetricName() {
        return metricName;
    }

    public int getNumInterfaces() {
        return numInterfaces;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public String getPayload() {
        StringBuilder payload = new StringBuilder();
        payload.append(metricName.toInteger()).append(";").append(numInterfaces);
        for (String iface : interfaces) {
            payload.append(";").append(iface);
        }
        return payload.toString();
    }

    @Override
    public String toString() {
        return "name= " + metricName.name() + ", numInterfaces= " + numInterfaces + ", interfaces= " + interfaces;
    }

    // método para coletar as estatísticas de todas as interfaces
    public String collectInterfaceStats() {
        StringBuilder stats = new StringBuilder();
        for (String iface : interfaces) {
            stats.append(iface).append(": ");
            try {
                stats.append(collectPackets(iface)).append(" packets");
            } catch (Exception e) {
                stats.append("error");
            }
            stats.append(", ");
        }
        return stats.toString();
    }

    // método para coletar o número de pacotes recebidos e enviados por uma interface
    private long collectPackets(String interfaceName) throws Exception {
        String statsPath = "/proc/net/dev";
        try (BufferedReader br = new BufferedReader(new FileReader(statsPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith(interfaceName + ":")) {
                    String[] parts = line.trim().split("\\s+");
                    long receivedPackets = Long.parseLong(parts[2]); // received packets
                    long sentPackets = Long.parseLong(parts[10]);    // transmitted packets
                    return receivedPackets + sentPackets;
                }
            }
        }
        throw new Exception();
    }
}
