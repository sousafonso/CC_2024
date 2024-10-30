package server;

import com.fasterxml.jackson.databind.ObjectMapper;
/*
 * A classe ObjectMapper é parte da biblioteca Jackson, que é uma das bibliotecas 
 * mais populares para manipulação de JSON em Java. Essa classe é usada para converter objetos 
 * Java para JSON e vice-versa (serialização e deserialização respetivamente).
 */
import java.io.File;
import java.util.List;

public class JSONTaskReader {
    private List<Device> devices;
    private String taskId;
    private String frequency;

    // Método para ler o ficheiro JSON
    public void readConfigFile(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JSONTaskReader jsonTaskReader = mapper.readValue(new File(path), JSONTaskReader.class);
            this.devices = jsonTaskReader.getDevices();
            this.taskId = jsonTaskReader.getTaskId();
            this.frequency = jsonTaskReader.getFrequency();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Ficheiro JSON lido com sucesso.");

        // Exibir os dispositivos e suas métricas
        for (Device device : devices) {
            System.out.println("Dispositivo: " + device.getDeviceId());
            System.out.println("Métricas:");
            System.out.println("  CPU: " + device.getDeviceMetrics().isCpuUsage());
            System.out.println("  RAM: " + device.getDeviceMetrics().isRamUsage());
            System.out.println("  Interfaces: " + device.getDeviceMetrics().getInterfaceStats());
        }

        System.out.println("Tarefa: " + taskId);

        System.out.println("Frequência: " + frequency);
}

    // Classe que representa um Dispositivo no JSON
    class Device {
        private String deviceId;
        private DeviceMetrics deviceMetrics;

        // Getters e Setters
        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public DeviceMetrics getDeviceMetrics() {
            return deviceMetrics;
        }

        public void setDeviceMetrics(DeviceMetrics deviceMetrics) {
            this.deviceMetrics = deviceMetrics;
        }
    }

    // Classe que representa as métricas do dispositivo no JSON
    class DeviceMetrics {
        private boolean cpuUsage;
        private boolean ramUsage;
        private List<String> interfaceStats;

        // Getters e Setters
        public boolean isCpuUsage() {
            return cpuUsage;
        }

        public void setCpuUsage(boolean cpuUsage) {
            this.cpuUsage = cpuUsage;
        }

        public boolean isRamUsage() {
            return ramUsage;
        }

        public void setRamUsage(boolean ramUsage) {
            this.ramUsage = ramUsage;
        }

        public List<String> getInterfaceStats() {
            return interfaceStats;
        }

        public void setInterfaceStats(List<String> interfaceStats) {
            this.interfaceStats = interfaceStats;
        }
    }

    class LinkMetrics {
        private Bandwidth bandwidth;
        private int jitter;
        private int packetLoss;
        private int latency;
    
        // Getters e Setters
        public Bandwidth getBandwidth() {
            return bandwidth;
        }
    
        public void setBandwidth(Bandwidth bandwidth) {
            this.bandwidth = bandwidth;
        }
    
        public int getJitter() {
            return jitter;
        }
    
        public void setJitter(int jitter) {
            this.jitter = jitter;
        }
    
        public int getPacketLoss() {
            return packetLoss;
        }
    
        public void setPacketLoss(int packetLoss) {
            this.packetLoss = packetLoss;
        }
    
        public int getLatency() {
            return latency;
        }
    
        public void setLatency(int latency) {
            this.latency = latency;
        }
    }
}
