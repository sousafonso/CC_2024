package taskContents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class LocalMetric {
    private MetricName metricName;
    private int numInterfaces;
    private List<String> interfaces;

    public LocalMetric(MetricName metricName, List<String> interfaces) {
        this.metricName = metricName;
        this.numInterfaces = interfaces.size();
        this.interfaces = interfaces;
    }

    public String getPayload() {
        StringBuilder payload = new StringBuilder();
        payload.append(metricName.toInteger()).append(";").append(numInterfaces);
        for (String iface : interfaces) {
            payload.append(";").append(iface);
        }
        return payload.toString();
    }

    @Override
    public String toString() {
        return "name= " + metricName.name() + ", numInterfaces= " + numInterfaces + ", interfaces= " + interfaces;
    }

    // Método para recolher uso de CPU
    public double collectCpuUsage() {
        return executeCommand(List.of("sh", "-c", "top -bn1 | grep 'Cpu(s)' | sed 's/.*, *\\([0-9.]*\\)%* id.*/\\1/' | awk '{print 100 - $1}'"));
    }

    // Método para recolher uso de RAM
    public double collectRamUsage() {
        return executeCommand(List.of("sh", "-c", "free | grep Mem | awk '{print $3/$2 * 100.0}'"));
    }

    // Método para recolher estatísticas das interfaces de rede
    public String collectInterfaceStats() {
        return executeCommandString(List.of("ip", "-s", "link"));
    }

    // @Override
    // public String toString() {
    //     return "name= " + metricName.name() + ", numInterfaces= " + numInterfaces + ", interfaces= " + interfaces;
    // }

    // Método genérico para executar comandos e retornar o resultado como double
    // RACIOCINIO: O método executeCommand recebe uma lista de strings chamada command e retorna um double.
    //  O método cria um objeto ProcessBuilder com o comando passado como argumento e inicia o processo.
    //  Em seguida, ele cria um BufferedReader para ler a saída do processo e armazena a saída em um StringBuilder.
    //  Por fim, o método aguarda a conclusão do processo e retorna a saída do processo como um double.
    private double executeCommand(List<String> command) {
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
        return Double.parseDouble(output.toString().trim());
    }

    // Método genérico para executar comandos e retornar o resultado como String
    // RACIOCINIO: O método executeCommandString recebe uma lista de strings chamada command e retorna uma string.
    //  O método cria um objeto ProcessBuilder com o comando passado como argumento e inicia o processo que será executado.
    //  Em seguida, ele cria um BufferedReader para ler a saída do processo e armazena a saída em um StringBuilder.
    //  Por fim, o método aguarda a conclusão do processo e retorna a saída do processo como uma string.
    private String executeCommandString(List<String> command) {
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
        return output.toString().trim();
    }
}
