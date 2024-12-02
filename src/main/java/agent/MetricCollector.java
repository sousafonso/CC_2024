package agent;

import message.Message;
import message.MessageType;
import message.Notification;
import message.TaskResult;
import taskContents.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return Double.parseDouble(executeCommand(List.of("sh",
                "-c",
                command.replaceAll("\\\\[$]", "\\$"))));
    }

    private double collectRAMUsage() throws RuntimeException{
        String command = "free -m | grep Mem | awk '{print \\$3/\\$2 * 100.0}'";
        return Double.parseDouble(executeCommand(List.of("sh",
                "-c",
                command.replaceAll("\\\\[$]", "\\$"))));
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

    private String executeCommand(List<String> command) throws RuntimeException{
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command); // ProcessBuilder é uma classe que permite a criação de processos
            Process process = processBuilder.start(); // start() inicia o processo

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println(command.get(2) + " failed with exit code: " + exitCode);
                throw new RuntimeException();
            }
        } catch (Exception e) {
            output.append("Error executing command: ").append(command);
        }

        return output.toString();
    }

    public double processLocalMetric() throws RuntimeException{
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

    private double calculateLatency() {
        Latency latency = (Latency) this.linkMetric;
        return executePingCommand(List.of("ping", "-c", String.valueOf(latency.getPackageQuantity()), "-i", String.valueOf(latency.getFrequency()), latency.getDestination()));
    }

    private double executePingCommand(List<String> command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Pattern pattern = Pattern.compile("rtt min/avg/max/mdev = [^/]*/([^/]*)/[^/]*/[^ ]* ms");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return Double.parseDouble(matcher.group(1));
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Ping failed with exit code -> " + exitCode);
            }
        } catch (Exception e) {
            System.err.println("Error executing command: " + command);
            e.printStackTrace();
            return Double.MIN_VALUE;
        }

        return Double.MIN_VALUE;
    }

    private double executeIperfCommand(List<String> command, MetricName metricName) {
        //TODO rever iperf (packet loss e jitter)
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            output.append("Error executing command: ").append(command);
        }

        // Parse the output to extract the jitter or packet loss value
        String[] lines = output.toString().split("\n");
        for (String line : lines) {
            if (line.contains("jitter")) {
                String[] parts = line.split(",");
                for (String part : parts) {
                    if (part.contains("jitter")) {
                        return Double.parseDouble(part.split(":")[1].trim());
                    }
                }
            } else if (line.contains("lost")) {
                String[] parts = line.split(",");
                for (String part : parts) {
                    if (part.contains("lost")) {
                        return Double.parseDouble(part.split(":")[1].trim());
                    }
                }
            }
        }
        return 0.0;
    }

    public double processLinkMetric() throws RuntimeException {
        double result = Double.MIN_VALUE;
        switch (linkMetric.getMetricName()) {
            case LATENCY:
                result = calculateLatency();
                break;
            case JITTER:
                //IperfMetric metric = (IperfMetric) this.linkMetric;
                //result = executeIperfCommand(List.of("iperf3", "-c", linkMetric.getDestination(), "-u", "-J"));
                break;
            case PACKET_LOSS:
                //IperfMetric metric = (IperfMetric) this.linkMetric;
                //result = executeIperfCommand(List.of("iperf3", "-c", linkMetric.getDestination(), "-u", "-J"));
                break;
            case BANDWIDTH:
                //IperfMetric metric = (IperfMetric) this.linkMetric;
                //result = executeIperfCommand(List.of("iperf3", "-c", linkMetric.getDestination(), "-u", "-J"));
                break;
            default:
                System.out.println("Erro ao coletar métrica local " + linkMetric.getMetricName());
                break;
        }

        return result;
    }

    private void sendTaskResult(TaskResult taskResult, LocalDateTime timestamp) {
        int seqNumber = new Random().nextInt(Integer.MAX_VALUE);
        Message msg = new Message(seqNumber, 0, MessageType.TaskResult, taskResult);
        byte[] byteMsg = msg.getPDU();
        connection.sendViaUDP(byteMsg);

        // Adicionar à lista de espera por ACK
        NMS_Agent.addAckToList(timestamp, msg);
    }

    private void sendAlertNotification(Notification notification) {
        int seqNumber = new Random().nextInt(Integer.MAX_VALUE);
        Message msg = new Message(seqNumber, 0, MessageType.Notification, notification);
        byte[] byteMsg = msg.getPDU();
        connection.sendViaTCP(byteMsg);

        // Adicionar à lista de espera por ACK
        NMS_Agent.addAckToList(notification.getTimestamp(), msg); // ??
    }

    public void run() {
        try {
            double result = Double.MIN_VALUE;
            MetricName name;
            if (localMetric != null) {
                //result = processLocalMetric();
                name = localMetric.getMetricName();
            } else {
                result = processLinkMetric();
                name = linkMetric.getMetricName();
            }

            if (result != Double.MIN_VALUE) {
                LocalDateTime timestamp = LocalDateTime.now();
                System.out.println("A enviar resultado ao servidor: " + taskID + "-" + name + " -> " + result + " [" + timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "]");
                sendTaskResult(new TaskResult(taskID, name, result), timestamp);
                if (this.alertValue >= 0) {
                    if (result > this.alertValue) {
                        System.out.println("A enviar notificação ao servidor: " + taskID + "-" + name + " -> " + result + " > " + alertValue + " [" + timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "]");
                        sendAlertNotification(new Notification(taskID, name, result, timestamp));
                    }
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Erro ao coletar as métricas: " + e.getMessage());
        }
    }
}