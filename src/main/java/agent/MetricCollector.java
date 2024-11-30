package agent;

import message.TaskResult;
import taskContents.LinkMetric;
import taskContents.LocalMetric;

import java.io.*;
import java.util.List;

public class MetricCollector implements Runnable {
    private LocalMetric localMetric;
    private LinkMetric linkMetric;

    public MetricCollector(LocalMetric localMetric, LinkMetric linkMetric) {
        this.localMetric = localMetric;
        this.linkMetric = linkMetric;
    }

    private void collectCpuUsage() {
        System.out.println("CPU Usage: " + executeCommand(List.of("sh",
                "-c",
                "top -b -n1 | grep 'Cpu(s)' | awk '{print 100 - \\$8}'")));
    }

    private void collectRAMUsage() {
        System.out.println("RAM Usage: " + executeCommand(List.of("sh",
                "-c",
                "free -m | grep Mem | awk '{print \\$3/\\$2 * 100.0}'")));
    }

    public long collectPackets(String interfaceName) throws IOException {
        String statsPath = "/proc/net/dev";

        try (BufferedReader br = new BufferedReader(new FileReader(statsPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith(interfaceName + ":")) {
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

    private String executeCommand(List<String> command) {
        StringBuilder output = new StringBuilder(); // StringBuilder é um tipo de String que pode ser modificada
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command); // ProcessBuilder é uma classe que permite a criação de processos
            Process process = processBuilder.start(); // start() inicia o processo

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            process.waitFor(); // waitFor() faz com que o processo atual aguarde a conclusão do processo representado por este Process
        } catch (Exception e) {
            e.printStackTrace();
            output.append("Error executing command: ").append(command);
        }

        return output.toString();
    }

    public void processLocalMetric(){
        switch(localMetric.getMetricName()){
            case CPU_USAGE:
                collectCpuUsage();
                break;
            case RAM_USAGE:
                collectRAMUsage();
                break;
            case INTERFACE_STATS:
                //TODO precisa de verificação
                for(String anInterface : localMetric.getInterfaces()) {
                    try {
                        collectPackets(anInterface);
                    } catch (IOException e) {
                        System.out.println("Erro ao medir pacotes por segundo na interface " + anInterface);
                    }
                }
                break;
            default:
                System.out.println("Erro ao coletar métrica local " + localMetric.getMetricName());
            break;
        }
        //TODO
        // processar tarefa
        // switch para cada tipo de tarefa
        // pegar no resultado
        // mandar resultado
    }

    public void processLinkMetric(){
        //TODO
        double result;
        switch (linkMetric.getMetricName()) {
            case LATENCY:
                result = linkMetric.calculateLatency();
                break;
            case JITTER:
                result = linkMetric.calculateJitter();
                break;
            case PACKET_LOSS:
                result = linkMetric.calculatePacketLoss();
                break;
            default:
                throw new IllegalArgumentException("Métrica desconhecida: " + linkMetric.getMetricName());
        }
        //sendTaskResult(new TaskResult(task.getId(), linkMetric.getMetricName(), String.valueOf(result)));
        System.out.println("Link Metric: " + linkMetric.getMetricName() + " -> " + result);
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