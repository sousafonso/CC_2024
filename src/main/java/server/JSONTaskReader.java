package server;



import com.fasterxml.jackson.databind.ObjectMapper;

import taskContents.Conditions;
import message.Task;
import taskContents.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONTaskReader {
    private final String filePath = "./config/config.json";
    private JsonTasks tasks;

    public JSONTaskReader() {}

    public Map<String, Task> readJson(){
        try{
            this.tasks = (new ObjectMapper()).readValue(new File(this.filePath), JsonTasks.class);
            return this.tasks.jsonTaskToTask();
        }
        catch(IOException e){
            System.out.println("Erro ao ler o ficheiro JSON");
            return null;
        }
    }
}

class JsonTasks{
    private List<JsonTask> tasks;

    public JsonTasks(List<JsonTask> tasks) {
        this.tasks = tasks;
    }

    public JsonTasks() {
        this.tasks = new ArrayList<JsonTask>();
    }

    public List<JsonTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<JsonTask> tasks) {
        this.tasks = tasks;
    }

    public String toString() {
        return tasks.toString();
    }

    public Map<String, Task> jsonTaskToTask(){
        Map<String, Task> taskMap = new HashMap<>();

        for(JsonTask jsonTask : this.tasks){ // ciclo que percorre todas as tarefas
            for(Device device : jsonTask.getDevices()){ // ciclo que percorre todos os dispositivos de cada tarefa
                String deviceID = device.getDeviceId();
                DeviceMetrics deviceMetrics = device.getDeviceMetrics();
                LinkMetrics linkMetrics = device.getLinkMetrics();
                AlertFlowConditions afc = device.getAlertFlowConditions();

                List<LocalMetric> localMetrics = new ArrayList<>();
                if(deviceMetrics != null){
                    if(deviceMetrics.isCpuUsage()){
                        localMetrics.add(new LocalMetric(MetricName.CPU_USAGE, new ArrayList<>()));
                    }

                    if(deviceMetrics.isRamUsage()){
                        localMetrics.add(new LocalMetric(MetricName.RAM_USAGE, new ArrayList<>()));
                    }

                    List<String> interfaces = deviceMetrics.getInterfaceStats();
                    if(interfaces != null && !interfaces.isEmpty()){
                        localMetrics.add(new LocalMetric(MetricName.INTERFACE_STATS, interfaces));
                    }
                }

                List<LinkMetric> linkMetricsList = new ArrayList<>();
                if(linkMetrics != null){
                    JsonIperfMetric bandwidth = linkMetrics.getBandwidth();
                    JsonIperfMetric jitter = linkMetrics.getJitter();
                    JsonIperfMetric packetLoss = linkMetrics.getPacketLoss();
                    JsonLatency latency = linkMetrics.getLatency();

                    if(bandwidth != null){
                        linkMetricsList.add(new IperfMetric(MetricName.BANDWIDTH, bandwidth.getServerAddress(), bandwidth.getRole().charAt(0), bandwidth.getDuration(), bandwidth.getProtocol()));
                    }

                    if(jitter != null){
                        linkMetricsList.add(new IperfMetric(MetricName.JITTER, jitter.getServerAddress(), jitter.getRole().charAt(0), jitter.getDuration(), jitter.getProtocol()));
                    }

                    if(packetLoss != null){
                        linkMetricsList.add(new IperfMetric(MetricName.PACKET_LOSS, packetLoss.getServerAddress(), packetLoss.getRole().charAt(0), packetLoss.getDuration(), packetLoss.getProtocol()));
                    }

                    if(latency != null){
                        linkMetricsList.add(new Latency(MetricName.LATENCY, latency.getServerAddress(), latency.getFrequency(), latency.getPackageCount()));
                    }
                }

                Conditions conditions = new Conditions(afc.getCpuUsage(), afc.getRamUsage(), afc.getInterfaceStats(), afc.getPacketLoss(), afc.getJitter());

                Task task = new Task(jsonTask.getTaskId(), jsonTask.getFrequency(), conditions, linkMetricsList, localMetrics);

                taskMap.put(deviceID, task);
            }
        }

        return taskMap;
    }
}

class JsonTask {
    private String taskId;
    private int frequency;
    private List<Device> devices;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return "JsonTask: {" +
                "taskId='" + taskId + '\'' +
                ", frequency=" + frequency +
                ", devices=" + devices +
                "}\n";
    }
}

class Device {
    private String deviceId;
    private DeviceMetrics deviceMetrics;
    private LinkMetrics linkMetrics;
    private AlertFlowConditions alertFlowConditions;

    public Device(String deviceId, DeviceMetrics deviceMetrics, LinkMetrics linkMetrics, AlertFlowConditions alertFlowConditions) {
        this.deviceId = deviceId;
        this.deviceMetrics = deviceMetrics;
        this.linkMetrics = linkMetrics;
        this.alertFlowConditions = alertFlowConditions;
    }

    public Device(){
        this.deviceId = null;
        this.deviceMetrics = null;
        this.linkMetrics = null;
        this.alertFlowConditions = new AlertFlowConditions();
    }

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

    public LinkMetrics getLinkMetrics() {
        return linkMetrics;
    }

    public void setLinkMetrics(LinkMetrics linkMetrics) {
        this.linkMetrics = linkMetrics;
    }

    public AlertFlowConditions getAlertFlowConditions() {
        return alertFlowConditions;
    }

    public void setAlertFlowConditions(AlertFlowConditions alertFlowConditions) {
        this.alertFlowConditions = alertFlowConditions;
    }

    public String toString() {
        return "Device: {" +
                "deviceId='" + deviceId + '\'' +
                ", deviceMetrics=" + deviceMetrics +
                ", linkMetrics=" + linkMetrics +
                ", alertFlowConditions=" + alertFlowConditions +
                "}\n";
    }
}

class DeviceMetrics {
    private boolean cpuUsage;
    private boolean ramUsage;
    private List<String> interfaceStats;

    public DeviceMetrics(boolean cpuUsage, boolean ramUsage, List<String> interfaceStats) {
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.interfaceStats = interfaceStats;
    }

    public DeviceMetrics() {
        this.cpuUsage = false;
        this.ramUsage = false;
        this.interfaceStats = null;
    }

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

    public String toString() {
        return "DeviceMetrics: {" +
                "cpuUsage=" + cpuUsage +
                ", ramUsage=" + ramUsage +
                ", interfaceStats=" + interfaceStats +
                '}';
    }
}

class LinkMetrics {
    private JsonIperfMetric bandwidth;
    private JsonIperfMetric jitter;
    private JsonIperfMetric packetLoss;
    private JsonLatency latency;

    public LinkMetrics(JsonIperfMetric bandwidth, JsonIperfMetric jitter, JsonIperfMetric packetLoss, JsonLatency latency) {
        this.bandwidth = bandwidth;
        this.jitter = jitter;
        this.packetLoss = packetLoss;
        this.latency = latency;
    }
    public LinkMetrics() {
        this.bandwidth = null;
        this.jitter = null;
        this.packetLoss = null;
        this.latency = null;
    }

    public JsonIperfMetric getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(JsonIperfMetric bandwidth) {
        this.bandwidth = bandwidth;
    }

    public JsonIperfMetric getJitter() {
        return jitter;
    }

    public void setJitter(JsonIperfMetric jitter) {
        this.jitter = jitter;
    }

    public JsonIperfMetric getPacketLoss() {
        return packetLoss;
    }

    public void setPacketLoss(JsonIperfMetric packetLoss) {
        this.packetLoss = packetLoss;
    }

    public JsonLatency getLatency() {
        return latency;
    }

    public void setLatency(JsonLatency latency) {
        this.latency = latency;
    }

    public String toString() {
        return "LinkMetrics: {" + "bandwidth=" + bandwidth + ", jitter=" + jitter + ", packetLoss=" + packetLoss + ", latency=" + latency + '}';
    }
}

class JsonIperfMetric {
    private String role;
    private String serverAddress;
    private int duration;
    private String protocol;

    public JsonIperfMetric(String role, String serverAddress, int duration, String protocol) {
        this.role = role;
        this.serverAddress = serverAddress;
        this.duration = duration;
        this.protocol = protocol;
    }

    public JsonIperfMetric() {
        this.role = null;
        this.serverAddress = null;
        this.duration = -1;
        this.protocol = null;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String toString(){
        return "JsonIperfMetric: { role=" + role + ", serverAddress=" + serverAddress + ", duration=" + duration + ", protocol=" + protocol + "}";
    }
}

class JsonLatency {
    private String serverAddress;
    private int frequency;
    private int packageCount;

    public JsonLatency(String serverAddress, int frequency, int packageCount) {
        this.serverAddress = serverAddress;
        this.frequency = frequency;
        this.packageCount = packageCount;
    }

    public JsonLatency() {
        this.serverAddress = null;
        this.frequency = -1;
        this.packageCount = -1;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public String toString(){
        return "latency: { serverAddress: " + serverAddress + ", frequency: " + frequency + ", packageCount: " + packageCount + "}";
    }
}

class AlertFlowConditions {
    private int cpuUsage;
    private int ramUsage;
    private int interfaceStats;
    private int packetLoss;
    private int jitter;
    private int latency;
    private int bandwidth;

    public AlertFlowConditions(int cpuUsage, int ramUsage, int interfaceStats, int packetLoss, int jitter, int latency, int bandwidth) {
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.interfaceStats = interfaceStats;
        this.packetLoss = packetLoss;
        this.jitter = jitter;
        this.latency = latency;
        this.bandwidth = bandwidth;
    }

    public AlertFlowConditions() {
        this.cpuUsage = -1;
        this.ramUsage = -1;
        this.interfaceStats = -1;
        this.packetLoss = -1;
        this.jitter = -1;
        this.latency = -1;
        this.bandwidth = -1;
    }

    public int getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(int cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public int getRamUsage() {
        return ramUsage;
    }

    public void setRamUsage(int ramUsage) {
        this.ramUsage = ramUsage;
    }

    public int getInterfaceStats() {
        return interfaceStats;
    }

    public void setInterfaceStats(int interfaceStats) {
        this.interfaceStats = interfaceStats;
    }

    public int getPacketLoss() {
        return packetLoss;
    }

    public void setPacketLoss(int packetLoss) {
        this.packetLoss = packetLoss;
    }

    public int getJitter() {
        return jitter;
    }

    public void setJitter(int jitter) {
        this.jitter = jitter;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String toString(){
        return "AlertFlowConditions: { cpuUsage: " + cpuUsage + ", ramUsage: " + ramUsage + ", interfaceStats: " + interfaceStats + ", packetLoss: " + packetLoss + ", jitter: " + jitter +", latency: " + latency + ", bandwidth: " + bandwidth + "}";
    }
}