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
    private String taskId;
    private int frequency;
    private List<Device> devices;

    // Método para ler o ficheiro JSON e deserializar os dados
    public void readConfigFile(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JSONTaskReader task = mapper.readValue(new File(filePath), JSONTaskReader.class);
            this.taskId = task.getTaskId();
            this.frequency = task.getFrequency();
            this.devices = task.getDevices();

            System.out.println("Tarefa lida: " + taskId + ", Frequência: " + frequency);
            for (Device device : devices) {
                System.out.println("Dispositivo: " + device.getDeviceId());
                System.out.println("A monitorizar CPU: " + device.getDeviceMetrics().isCpuUsage());
                System.out.println("A monitorizar RAM: " + device.getDeviceMetrics().isRamUsage());
                System.out.println("Interfaces: " + device.getDeviceMetrics().getInterfaceStats());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters
    public String getTaskId() {
        return taskId;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<Device> getDevices() {
        return devices;
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
