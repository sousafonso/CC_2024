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
    public List<Task> readConfigFile(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Config config = mapper.readValue(new File(filePath), Config.class);
            return config.getTasks();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }   
        
        
    }

    class Config {
        private List<Task> tasks;
    
        public List<Task> getTasks() {
            return tasks;
        }
    
        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
        }
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
