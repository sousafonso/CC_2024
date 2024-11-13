package agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import message.*;

public class MetricCollector {

    // public Data collectPing(String target) {
    //     return executeCommand(List.of("ping", "-c", "4", target));
    // }

    // public Data collectIperf(String server) {
    //     return executeCommand(List.of("iperf3", "-c", server));
    // }

    // public Data collectNetworkInterfaces() {
    //     return executeCommand(List.of("ip", "-s", "link"));
    // }

    // private Data executeCommand(List<String> command) {
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
    //     return new Data(command.toString(), output.toString());
    // }

    public PingData collectPing(String target) {
        return (PingData) executeCommand(List.of("ping", "-c", "4", target), PingData.class);
    }

    public IperfData collectIperf(String server) {
        return (IperfData) executeCommand(List.of("iperf3", "-c", server), IperfData.class);
    }

    public NetworkInterfacesData collectNetworkInterfaces() {
        return (NetworkInterfacesData) executeCommand(List.of("ip", "-s", "link"), NetworkInterfacesData.class);
    }

    private Data executeCommand(List<String> command, Class<? extends Data> dataType) {
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

        if (dataType == PingData.class) {
            return new PingData(command.toString(), output.toString());
        } else if (dataType == IperfData.class) {
            return new IperfData(command.toString(), output.toString());
        } else if (dataType == NetworkInterfacesData.class) {
            return new NetworkInterfacesData(command.toString(), output.toString());
        } else {
            throw new IllegalArgumentException("Unknown data type");
        }
    }
}