package taskContents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public abstract class LinkMetric {
    private MetricName metricName; // nome da métrica 
    private String destination; // destino da métrica

    public LinkMetric(MetricName metricName, String destination) {
        this.metricName = metricName;
        this.destination = destination;
    }

    public MetricName getMetricName() {
        return metricName;
    }

    public String getDestination() {
        return destination;
    }

    public abstract String getPayload();

    public double calculateJitter() {
        return executeIperfCommand(List.of("iperf3", "-c", destination, "-u", "-J"));
    }

    public double calculatePacketLoss() {
        return executeIperfCommand(List.of("iperf3", "-c", destination, "-u", "-J"));
    }

    public double calculateLatency() {
        return executePingCommand(List.of("ping", "-c", "4", destination));
    }

    private double executePingCommand(List<String> command) {
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

        // Parse the output to extract the latency value
        String[] lines = output.toString().split("\n");
        for (String line : lines) {
            if (line.contains("avg")) {
                String[] parts = line.split("/");
                return Double.parseDouble(parts[4].trim());
            }
        }
        return 0.0;
    }

    private double executeIperfCommand(List<String> command) {
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
}
