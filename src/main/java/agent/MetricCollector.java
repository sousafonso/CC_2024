package agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MetricCollector {

    /*public Data collectPing(String target) {
        return executeCommand(List.of("ping", "-c", "4", target));
    }

    public Data collectIperf(String server) {
        return executeCommand(List.of("iperf3", "-c", server));
    }

    public Data collectNetworkInterfaces() {
        return executeCommand(List.of("ip", "-s", "link"));
    }

    private Data executeCommand(List<String> command) {
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
        return new Data(command.toString(), output.toString());
    }*/
}