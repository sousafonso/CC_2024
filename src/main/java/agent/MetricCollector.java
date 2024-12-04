package agent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
    private static Lock iperfServerLock = new ReentrantLock();
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

    private double collectCPUorRAMUsage(String command) throws RuntimeException {
        try {
            List<String> result = executeCommand(List.of("sh",
                    "-c",
                    command.replaceAll("\\\\[$]", "\\$")));

            return result.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(result.getFirst().replaceAll(",", "."));
        } catch (RuntimeException e) {
            System.err.println("Medição de " + localMetric.getMetricName() + " falhou: " + e.getMessage());
        }

        return Double.MIN_VALUE;
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
        try {
            List<String> result = executeCommand(List.of("ping",
                    "-c", String.valueOf(latency.getPackageQuantity()),
                    "-i", String.valueOf(latency.getFrequency()),
                    latency.getDestination()));

            Pattern pattern = Pattern.compile("rtt min/avg/max/mdev = [^/]*/([^/]*)/[^/]*/[^ ]* ms");

            for (String line : result) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return Double.parseDouble(matcher.group(1));
                }
            }
        } catch (RuntimeException e) {
            System.err.println("Medição da latência falhou: " + e.getMessage());
        }

        return Double.MIN_VALUE;
    }

    private double calculateIperfMetric(MetricName name, Pattern pattern) throws RuntimeException{
        iperfServerLock.lock();
        try {
            IperfMetric metric = (IperfMetric) this.linkMetric;
            List<String> result;
            if (name == MetricName.BANDWIDTH) {
                List<String> command = new ArrayList<>(List.of("iperf3",
                        "-c", linkMetric.getDestination(),
                        "-t", String.valueOf(metric.getDuration()),
                        "-f", "m"));

                if(metric.getProtocol().equals("UDP")){
                    command.add("-u");
                }

                result = executeCommand(command);
            } else {
                if (!metric.getProtocol().equals("UDP")) {
                    System.err.println("Medição de Jitter e Packet Loss deve ser feita com protocolo UDP");
                }

                result = executeCommand(List.of("iperf3",
                        "-c", linkMetric.getDestination(),
                        "-t", String.valueOf(metric.getDuration()),
                        "-u"));
            }

            for (String line : result) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return Double.parseDouble(matcher.group(1));
                }
            }

        } catch (RuntimeException e) {
            System.err.println("Medição de " + name + " falhou: " + e.getMessage());
        }
        finally {
            iperfServerLock.unlock();
        }

        return Double.MIN_VALUE;
    }

    private void runIperfServer(){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(List.of("iperf3", "-s"));
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
                result = collectCPUorRAMUsage("top -b -n1 | grep 'Cpu(s)' | awk '{print 100 - \\$8}'");
                break;
            case RAM_USAGE:
                result = collectCPUorRAMUsage("free -m | grep Mem | awk '{print \\$3/\\$2 * 100.0}'");
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
                    result = calculateIperfMetric(linkMetric.getMetricName(), Pattern.compile("(\\d+\\.\\d+)\\s*ms.*receiver$"));
                }
                break;
            case PACKET_LOSS:
                iMetric = (IperfMetric) this.linkMetric;
                if(iMetric.getRole() == 's'){
                    runIperfServer();
                }
                else {
                    result = calculateIperfMetric(linkMetric.getMetricName(), Pattern.compile("\\((\\d+)%\\)\\s*(receiver)$"));
                }
                break;
            case BANDWIDTH:
                iMetric = (IperfMetric) this.linkMetric;
                if(iMetric.getRole() == 's'){
                    runIperfServer();
                }
                else {
                    result = calculateIperfMetric(linkMetric.getMetricName(), Pattern.compile("(\\d+\\.?\\d*)\\s*Mbits/sec\\s*(receiver)$"));
                    System.out.println("Medição de Bandwidth -> " + result);
                }
                break;
            default:
                System.out.println("Erro ao coletar métrica do link " + linkMetric.getMetricName());
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
                throw new RuntimeException(String.join(" ", lines));
            }
        } catch (Exception e) {
            throw new RuntimeException(String.join(" ", lines));
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
    }

    public void run() {
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
    }
}