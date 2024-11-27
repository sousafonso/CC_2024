package agent;

import taskContents.LinkMetric;
import taskContents.LocalMetric;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.MemoryMXBean;
import java.io.*;
import java.util.List;

public class MetricCollector implements Runnable {
    //int frequency;
    private LocalMetric localMetric;
    private LinkMetric linkMetric;

    public MetricCollector(LocalMetric localMetric, LinkMetric linkMetric) {
        this.localMetric = localMetric;
        this.linkMetric = linkMetric;
    }

    private void getCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;

            double systemCpuLoad = sunOsBean.getCpuLoad();

            System.out.println("System CPU Load: " + (systemCpuLoad * 100) + "%");
        }
        else{
            System.out.println("Erro ao medir utilização do CPU");
        }
    }

    private long bytesToMegabytes(long bytes) {
        return bytes / (1024 * 1024);
    }

    private void getRAMUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;

            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

            long totalPhysicalMemory = sunOsBean.getTotalPhysicalMemorySize();
            long freePhysicalMemory = sunOsBean.getFreePhysicalMemorySize();
            long usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;

            System.out.println("Total Physical Memory: " +
                    bytesToMegabytes(totalPhysicalMemory) + " MB");
            System.out.println("Free Physical Memory: " +
                    bytesToMegabytes(freePhysicalMemory) + " MB");
            System.out.println("Used Physical Memory: " +
                    bytesToMegabytes(usedPhysicalMemory) + " MB");
        }
        else{
            System.out.println("Erro ao medir utilização da RAM");
        }
    }

    public long getPacketsPerSecond(String interfaceName) throws IOException {
        String statsPath = "/proc/net/dev";

        try (BufferedReader br = new BufferedReader(new FileReader(statsPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith(interfaceName + ":")) {
                    // Split the line and parse packets
                    String[] parts = line.trim().split("\\s+");
                    long receivedPackets = Long.parseLong(parts[2]); // received packets
                    long sentPackets = Long.parseLong(parts[10]);    // transmitted packets
                    System.out.println("Interface: " + interfaceName + " -> Received: " + receivedPackets + " Packets sent: " + sentPackets);
                    return receivedPackets + sentPackets;
                }
            }
        }
        throw new IOException();
    }

    public void processLocalMetric(){
        switch(localMetric.getMetricName()){
            case CPU_USAGE:
                getCpuUsage();
                break;
            case RAM_USAGE:
                getRAMUsage();
                break;
            case INTERFACE_STATS:
                //TODO precisa de verificação
                for(String anInterface : localMetric.getInterfaces()) {
                    try {
                        getPacketsPerSecond(anInterface);
                    } catch (IOException e) {
                        System.out.println("Erro ao medir pacotes por segundo na interface " + anInterface);
                    }
                }
                break;
            default:
                System.out.println("Erro ao coletar métrica local " + localMetric.getMetricName());
            break;
        }
        // processar tarefa
        // switch para cada tipo de tarefa
        // pegar no resultado
        // mandar resultado
    }

    public void processLinkMetric(){

    }

    public void run() {
        if (localMetric != null) {
            processLocalMetric();
        }
        else{
            processLinkMetric();
        }
    }
}