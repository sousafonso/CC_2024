package server;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.databind.*;

import message.Task;

public class JSONTaskReader {
    private final String filePath = "./config/config.json";
    private JsonTasks tasks;

    public JSONTaskReader() {
    }

    public  Map<String, Task> readJson(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try{
            this.tasks = mapper.readValue(new File(this.filePath), JsonTasks.class);
            System.out.println(this.tasks);

        }
        catch(IOException e){
            System.out.println("Erro ao ler o ficheiro JSON");
            e.printStackTrace();

        }
        return null;
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
}

class JsonTask {
    private String task_id;
    private int frequency;
    private List<Device> devices;

    public String getTaskId() {
        return task_id;
    }

     public void setTaskId(String task_id) {
        this.task_id = task_id;
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
                "task_id='" + task_id + '\'' +
                ", frequency=" + frequency +
                ", devices=" + devices +
                '}';
    }
}

class Device {
    private String device_id;
    private DeviceMetrics devic_metrics;
    private LinkMetrics link_metrics;
    private AlertFlowConditions alertflow_conditions;

    public Device(String device_id, DeviceMetrics devic_metrics, LinkMetrics link_metrics, AlertFlowConditions alertflow_conditions) {
        this.device_id = device_id;
        this.devic_metrics = devic_metrics;
        this.link_metrics = link_metrics;
        this.alertflow_conditions = alertflow_conditions;
    }

    public Device(){
        this.device_id = null;
        this.devic_metrics = null;
        this.link_metrics = null;
        this.alertflow_conditions = null;
    }

    public String getDeviceId() {
        return device_id;
    }

    public void setDeviceId(String device_id) {
        this.device_id = device_id;
    }

    public DeviceMetrics getDeviceMetrics() {
        return devic_metrics;
    }

    public void setDeviceMetrics(DeviceMetrics devic_metrics) {
        this.devic_metrics = devic_metrics;
    }

    public LinkMetrics getLinkMetrics() {
        return link_metrics;
    }

    public void setLinkMetrics(LinkMetrics link_metrics) {
        this.link_metrics = link_metrics;
    }

    public AlertFlowConditions getAlertflowConditions() {
        return alertflow_conditions;
    }

    public void setAlertflowConditions(AlertFlowConditions alertflow_conditions) {
        this.alertflow_conditions = alertflow_conditions;
    }

    public String toString() {
        return "Device: {" +
                "device_id='" + device_id + '\'' +
                ", devic_metrics=" + devic_metrics +
                ", link_metrics=" + link_metrics +
                ", alertflow_conditions=" + alertflow_conditions +
                "}\n";
    }
}

class DeviceMetrics {
    private boolean cpu_usage;
    private boolean ram_usage;
    private List<String> interface_stats;

    public DeviceMetrics(boolean cpu_usage, boolean ram_usage, List<String> interface_stats) {
        this.cpu_usage = cpu_usage;
        this.ram_usage = ram_usage;
        this.interface_stats = interface_stats;
    }

    public DeviceMetrics() {
        this.cpu_usage = false;
        this.ram_usage = false;
        this.interface_stats = null;
    }

    public boolean isCpuUsage() {
        return cpu_usage;
    }

    public void setCpuUsage(boolean cpu_usage) {
        this.cpu_usage = cpu_usage;
    }

    public boolean isRamUsage() {
        return ram_usage;
    }

    public void setRamUsage(boolean ram_usage) {
        this.ram_usage = ram_usage;
    }

    public List<String> getInterfaceStats() {
        return interface_stats;
    }

    public void setInterfaceStats(List<String> interface_stats) {
        this.interface_stats = interface_stats;
    }

    public String toString() {
        return "DeviceMetrics: {" +
                "cpu_usage=" + cpu_usage +
                ", ram_usage=" + ram_usage +
                ", interface_stats=" + interface_stats +
                '}';
    }
}

class LinkMetrics {
    private IperfMetric bandwidth;
    private IperfMetric jitter;
    private IperfMetric packet_loss;
    private Latency latency;

    public LinkMetrics(IperfMetric bandwidth, IperfMetric jitter, IperfMetric packet_loss, Latency latency) {
        this.bandwidth = bandwidth;
        this.jitter = jitter;
        this.packet_loss = packet_loss;
        this.latency = latency;
    }
    public LinkMetrics() {
        this.bandwidth = null;
        this.jitter = null;
        this.packet_loss = null;
        this.latency = null;
    }

    public IperfMetric getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(IperfMetric bandwidth) {
        this.bandwidth = bandwidth;
    }

    public IperfMetric getJitter() {
        return jitter;
    }

    public void setJitter(IperfMetric jitter) {
        this.jitter = jitter;
    }

    public IperfMetric getPacketLoss() {
        return packet_loss;
    }

    public void setPacketLoss(IperfMetric packet_loss) {
        this.packet_loss = packet_loss;
    }

    public Latency getLatency() {
        return latency;
    }

    public void setLatency(Latency latency) {
        this.latency = latency;
    }

    public String toString() {
        return "LinkMetrics: {" + "bandwidth=" + bandwidth + ", jitter=" + jitter + ", packet_loss=" + packet_loss + ", latency=" + latency + '}';
    }
}

class IperfMetric{
    private String role;
    private String server_address;
    private int duration;
    private String protocol;

    public IperfMetric(String role, String server_address, int duration, String protocol) {
        this.role = role;
        this.server_address = server_address;
        this.duration = duration;
        this.protocol = protocol;
    }

    public IperfMetric() {
        this.role = null;
        this.server_address = null;
        this.duration = 0;
        this.protocol = null;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getServerAddress() {
        return server_address;
    }

    public void setServerAddress(String server_address) {
        this.server_address = server_address;
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
        return "IperfMetric: { role=" + role + ", server_address=" + server_address + ", duration=" + duration + ", protocol=" + protocol + "}";
    }
}

class Latency{
    private int frequency;
    private int package_count;

    public Latency(int frequency, int package_count) {
        this.frequency = frequency;
        this.package_count = package_count;
    }

    public Latency() {
        this.frequency = 0;
        this.package_count = 0;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getPackageQuantity() {
        return package_count;
    }

    public void setPackageQuantity(int package_count) {
        this.package_count = package_count;
    }

    public String toString(){
        return "latency: { frequency: " + frequency + ", package_count: " + package_count + "}";
    }
}

class AlertFlowConditions {
    private int cpu_usage;
    private int ram_usage;
    private int interface_stats;
    private int packet_loss;
    private int jitter;

    public AlertFlowConditions(int cpu_usage, int ram_usage, int interface_stats, int packet_loss, int jitter){
        this.cpu_usage = cpu_usage;
        this.ram_usage = ram_usage;
        this.interface_stats = interface_stats;
        this.packet_loss = packet_loss;
        this.jitter = jitter;
    }

    public AlertFlowConditions() {
        this.cpu_usage = 0;
        this.ram_usage = 0;
        this.interface_stats = 0;
        this.packet_loss = 0;
        this.jitter = 0;
    }

    public int getCpuUsage() {
        return cpu_usage;
    }

    public void setCpuUsage(int cpu_usage) {
        this.cpu_usage = cpu_usage;
    }

    public int getRamUsage() {
        return ram_usage;
    }

    public void setRamUsage(int ram_usage) {
        this.ram_usage = ram_usage;
    }

    public int getInterfaceStats() {
        return interface_stats;
    }

    public void setInterfaceStats(int interface_stats) {
        this.interface_stats = interface_stats;
    }

    public int getPacketLoss() {
        return packet_loss;
    }

    public void setPacketLoss(int packet_loss) {
        this.packet_loss = packet_loss;
    }

    public int getJitter() {
        return jitter;
    }

    public void setJitter(int jitter) {
        this.jitter = jitter;
    }

    public String toString(){
        return "AlertFlowConditions: { cpu_usage: " + cpu_usage + ", ram_usage: " + ram_usage + ", interface_stats: " + interface_stats + ", packet_loss: " + packet_loss + ", jitter: " + jitter + "}";
    }

}


