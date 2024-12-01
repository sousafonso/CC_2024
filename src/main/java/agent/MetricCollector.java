package agent;

import message.Message;
import message.MessageType;
import message.Notification;
import message.TaskResult;
import taskContents.LinkMetric;
import taskContents.LocalMetric;
import taskContents.MetricName;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

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

    private double collectCpuUsage() {
        String command = "top -b -n1 | grep 'Cpu(s)' | awk '{print 100 - \\$8}'";
        return Double.parseDouble(executeCommand(List.of("sh",
                "-c",
                command.replaceAll("\\\\[$]", "\\$"))));
    }

    private double collectRAMUsage() {
        String command = "free -m | grep Mem | awk '{print \\$3/\\$2 * 100.0}'";
        return Double.parseDouble(executeCommand(List.of("sh",
                "-c",
                command.replaceAll("\\\\[$]", "\\$"))));
    }

    public double collectPackets(String interfaceName) throws IOException {
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
                    output.append(line);
                }
            }

            process.waitFor(); // waitFor() faz com que o processo atual aguarde a conclusão do processo representado por este Process
        } catch (Exception e) {
            e.printStackTrace();
            output.append("Error executing command: ").append(command);
        }

        return output.toString();
    }

    public double processLocalMetric(){
        double result = Double.MIN_VALUE;
        switch(localMetric.getMetricName()){
            case CPU_USAGE:
                result = collectCpuUsage();
                break;
            case RAM_USAGE:
                result = collectRAMUsage();
                break;
            case INTERFACE_STATS:
                for(String anInterface : localMetric.getInterfaces()) {
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

        //TODO Lista de espera de ACKs (a mesma que netTask ou separada?)
    }

    public void run() {
        double result;
        MetricName name;
        if (localMetric != null) {
            result = processLocalMetric();
            name = localMetric.getMetricName();
        }
        else{
            result = 0;
            //result = processLinkMetric();
            name = linkMetric.getMetricName();
        }

        if (result != Double.MIN_VALUE){
            LocalDateTime timestamp = LocalDateTime.now();
            System.out.println("A enviar resultado ao servidor: " + taskID + "-" + name + " -> " + result + " [" + timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "]");
            sendTaskResult(new TaskResult(taskID, name, result), timestamp);
            if(this.alertValue >= 0){
                if(result > this.alertValue){
                    System.out.println("A enviar notificação ao servidor: " + taskID + "-" + name + " -> " + result + " > " + alertValue + " [" + timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "]");
                    sendAlertNotification(new Notification(taskID, name, result, timestamp));
                }
            }
        }
    }
}