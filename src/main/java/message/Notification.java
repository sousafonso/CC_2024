package message;

public class Notification extends Data {
    private int cpuUsage;
    private int ramUsage;
    private int interfaceStats; // limite de pacotes opr segundo para as interfaces (limite de 2000 pacotes por segundo, por exemplo)
    private int packetLoss;
    private int jitter;

    public Notification(int cpuUsage, int ramUsage, int interfaceStats, int packetLoss, int jitter) {
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.interfaceStats = interfaceStats;
        this.packetLoss = packetLoss;
        this.jitter = jitter;
    }

    public Notification(String[] fields, int startIndex){
        this.cpuUsage = Integer.parseInt(fields[startIndex++]);
        this.ramUsage = Integer.parseInt(fields[startIndex++]);
        this.interfaceStats = Integer.parseInt(fields[startIndex++]);
        this.packetLoss = Integer.parseInt(fields[startIndex++]);
        this.jitter = Integer.parseInt(fields[startIndex]);
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

    public String getPayload() {
        return cpuUsage + ";" +
                ramUsage + ";" +
                interfaceStats + ";" +
                packetLoss + ";" +
                jitter;
    }
}
