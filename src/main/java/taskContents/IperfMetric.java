package taskContents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class IperfMetric extends LinkMetric{
    private char role;
    private int duration;
    private String protocol;

    public IperfMetric(MetricName name, String destination, char role, int duration, String protocol) {
        super(name, destination);
        this.role = role;
        this.duration = duration;
        this.protocol = protocol;
    }

    public String getPayload() {
        return super.getMetricName().toInteger() + ";" +
                super.getDestination() + ";" +
                role + ";" +
                duration  + ";" +
                protocol;
    }

    public String toString() {
        return "name= " + super.getMetricName().name() + ", destination" + super.getDestination() + ", role= " + role + ", duration= " + duration + ", protocol= " + protocol;
    }
    

    // PODEMOS POR ISTO AQUI
    // public double calculateJitter() {
    //     return executeIperfCommand(List.of("iperf3", "-c", getDestination(), "-u", "-J"));
    // }

    // public double calculatePacketLoss() {
    //     return executeIperfCommand(List.of("iperf3", "-c", getDestination(), "-u", "-J"));
    // }

    // private double executeIperfCommand(List<String> command) {
    //     StringBuilder output = new StringBuilder();
    //     try {
    //         ProcessBuilder processBuilder = new ProcessBuilder(command);
    //         Process process = processBuilder.start();

    //         try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
    //             String line;
    //             while ((line = reader.readLine()) != null) {
    //                 output.append(line).append("\n");
    //             }
    //         }

    //         process.waitFor();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         output.append("Error executing command: ").append(command);
    //     }

    //     // Parse the output to extract the jitter or packet loss value
    //     String[] lines = output.toString().split("\n");
    //     for (String line : lines) {
    //         if (line.contains("jitter")) {
    //             String[] parts = line.split(",");
    //             for (String part : parts) {
    //                 if (part.contains("jitter")) {
    //                     return Double.parseDouble(part.split(":")[1].trim());
    //                 }
    //             }
    //         } else if (line.contains("lost")) {
    //             String[] parts = line.split(",");
    //             for (String part : parts) {
    //                 if (part.contains("lost")) {
    //                     return Double.parseDouble(part.split(":")[1].trim());
    //                 }
    //             }
    //         }
    //     }
    //     return 0.0;
    // }
}
