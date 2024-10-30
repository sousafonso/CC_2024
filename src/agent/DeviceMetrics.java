import java.util.List;

public class DeviceMetrics {
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