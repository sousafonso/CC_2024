package agent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import message.Message;
import message.MessageType;
import message.Notification;
import message.TaskResult;
import taskContents.IperfMetric;
import taskContents.Latency;
import taskContents.LinkMetric;
import taskContents.LocalMetric;
import taskContents.MetricName;

public class MetricCollector implements Runnable {
    private Connection connection;
    private String taskID;
    private int alertValue;
    private LocalMetric localMetric;
    private LinkMetric linkMetric;

    public MetricCollector(Connection connection, String taskID, int alertValue, LocalMetric localMetric, LinkMetric linkMetric) {
        this.connection = connection;
        this.taskID = taskID;
        this.alertValue = alertValue;
        this.localMetric = localMetric;
        this.linkMetric = linkMetric;
    }

    private double collectCpuUsage() throws RuntimeException{
        String command = "top -b -n1 | grep 'Cpu(s)' | awk '{print 100 - \\$8}'";
        List<String> result = executeCommand(List.of("sh",
                "-c",
                command.replaceAll("\\\\[$]", "\\$")));

        return result.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(result.getFirst().replaceAll(",", "."));
    }

    private double collectRAMUsage() throws RuntimeException{
        String command = "free -m | grep Mem | awk '{print \\$3/\\$2 * 100.0}'";
        List<String> result = executeCommand(List.of("sh",
                "-c",
                command.replaceAll("\\\\[$]", "\\$")));

        return result.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(result.getFirst().replaceAll(",", "."));
    }

    private double collectPackets(String interfaceName) throws IOException {
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

    private double calculateLatency() throws RuntimeException{
        Latency latency = (Latency) this.linkMetric;
        List<String> result = executeCommand(List.of("ping",
                "-c", String.valueOf(latency.getPackageQuantity()),
                "-i", String.valueOf(latency.getFrequency()),
                latency.getDestination()));
        if(result.isEmpty()){
            return Double.MIN_VALUE;
        }
        Pattern pattern = Pattern.compile("rtt min/avg/max/mdev = [^/]*/([^/]*)/[^/]*/[^ ]* ms");

        for(String line : result) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                return Double.parseDouble(matcher.group(1));
            }
        }

        return Double.MIN_VALUE;
    }

    private double calculateIperfMetric(MetricName name, Pattern pattern) throws RuntimeException{
        IperfMetric metric = (IperfMetric) this.linkMetric;
        List<String> result;
        if(name == MetricName.BANDWIDTH){
            result = executeCommand(List.of("iperf3",
                "-c", linkMetric.getDestination(),
                "-t", String.valueOf(metric.getDuration())));
        }
        else{
            if(!metric.getProtocol().equals("UDP")){
                System.err.println("Medição de Jitter e Packet Loss deve ser feita com protocolo UDP");
            }

            result = executeCommand(List.of("iperf3",
                    "-c", linkMetric.getDestination(),
                    "-t", String.valueOf(metric.getDuration()),
                    "-u"));
        }

        for(String line : result) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                return Double.parseDouble(matcher.group(1));
            }
        }

        return Double.MIN_VALUE;
    }

    private void runIperfServer(){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(List.of("iperf", "-s"));
            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Arranque de servidor Iperf falhou com código: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Arranque de servidor Iperf falhou.");
        }
    }

    private double processLocalMetric() throws RuntimeException{
        double result = Double.MIN_VALUE;
        switch (localMetric.getMetricName()) {
            case CPU_USAGE:
                result = collectCpuUsage();
                break;
            case RAM_USAGE:
                result = collectRAMUsage();
                break;
            case INTERFACE_STATS:
                for (String anInterface : localMetric.getInterfaces()) {
                    try {
                        result = collectPackets(anInterface);
                    } catch (IOException e) {
                        System.out.println("Erro ao medir pacotes por segundo na interface " + anInterface);
                    }
                }
                break;
            default:
                System.out.println("Erro ao coletar métrica local " + localMetric.getMetricName());
                break;
        }

        return result;
    }

    private double processLinkMetric() throws RuntimeException {
        double result = Double.MIN_VALUE;
        IperfMetric iMetric;
        switch (linkMetric.getMetricName()) {
            case LATENCY:
                result = calculateLatency();
                break;
            case JITTER:
                iMetric = (IperfMetric) this.linkMetric;
                if(iMetric.getRole() == 's'){
                    runIperfServer();
                }
                else {
                    result = calculateIperfMetric(linkMetric.getMetricName(), Pattern.compile("(\\d+\\.\\d+)\\s*ms"));
                    System.out.println("Medição de Jitter -> " + result);
                }
                break;
            case PACKET_LOSS:
                iMetric = (IperfMetric) this.linkMetric;
                if(iMetric.getRole() == 's'){
                    runIperfServer();
                }
                else {
                    result = calculateIperfMetric(linkMetric.getMetricName(), Pattern.compile("\\((\\d+)%\\)"));
                    System.out.println("Medição de PacketLoss -> " + result);
                }
                break;
            case BANDWIDTH:
                iMetric = (IperfMetric) this.linkMetric;
                if(iMetric.getRole() == 's'){
                    runIperfServer();
                }
                else {
                    result = calculateIperfMetric(linkMetric.getMetricName(), Pattern.compile("(\\d+\\.\\d+)\\s*Mbits/sec"));
                    System.out.println("Medição de Bandwidth -> " + result);
                }
                break;
            default:
                System.out.println("Erro ao coletar métrica local " + linkMetric.getMetricName());
                break;
        }

        return result;
    }

    private List<String> executeCommand(List<String> command) throws RuntimeException{
        List<String> lines = new ArrayList<>();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command); // ProcessBuilder é uma classe que permite a criação de processos
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start(); // start() inicia o processo

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Comando " + command + " falhou com exit code: " + exitCode);
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar comando");
        }

        return lines;
    }

    private void sendTaskResult(TaskResult taskResult, LocalDateTime timestamp) {
        int seqNumber = new Random().nextInt(Integer.MAX_VALUE);
        Message msg = new Message(seqNumber, 0, MessageType.TaskResult, taskResult);
        byte[] byteMsg = msg.getPDU();
        connection.sendViaUDP(byteMsg);

        // Adicionar à lista de espera por ACK
        NMS_Agent.addToAckWaitingList(timestamp, msg);
    }

    private void sendAlertNotification(Notification notification) {
        int seqNumber = new Random().nextInt(Integer.MAX_VALUE);
        Message msg = new Message(seqNumber, 0, MessageType.Notification, notification);
        byte[] byteMsg = msg.getPDU();
        connection.sendViaTCP(byteMsg);

        // Adicionar à lista de espera por ACK
        NMS_Agent.addToAckWaitingList(notification.getTimestamp(), msg); // ??
    }

    public void run() {
        try {
            double result;
            MetricName name;
            if (localMetric != null) {
                result = processLocalMetric();
                name = localMetric.getMetricName();
            } else {
                result = processLinkMetric();
                name = linkMetric.getMetricName();
            }

            if (result != Double.MIN_VALUE) {
                LocalDateTime timestamp = LocalDateTime.now();
                System.out.println("[ENVIO] Resultado ao servidor: " + taskID + "-" + name + " -> " + result);
                sendTaskResult(new TaskResult(taskID, name, result), timestamp);
                if (this.alertValue >= 0) {
                    if (result > this.alertValue) {
                        System.out.println("[ENVIO] Notificação ao servidor: " + taskID + "-" + name + " -> " + result + " > " + alertValue);
                        sendAlertNotification(new Notification(taskID, name, result, timestamp));
                    }
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Erro ao coletar as métricas: " + e.getMessage());
        }
    }
}