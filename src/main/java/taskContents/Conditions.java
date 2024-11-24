package taskContents;

public class Conditions {
    private int cpuUsage;
    private int ramUsage;
    private int interfaceStats; // limite de pacotes opr segundo para as interfaces (limite de 2000 pacotes por segundo, por exemplo)
    private int packetLoss;
    private int jitter;

    public Conditions(int cpuUsage, int ramUsage, int interfaceStats, int packetLoss, int jitter) {
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.interfaceStats = interfaceStats;
        this.packetLoss = packetLoss;
        this.jitter = jitter;
    }

    public Conditions(String[] fields, int startIndex){
        this.cpuUsage = Integer.parseInt(fields[startIndex++]);
        this.ramUsage = Integer.parseInt(fields[startIndex++]);
        this.interfaceStats = Integer.parseInt(fields[startIndex++]);
        this.packetLoss = Integer.parseInt(fields[startIndex++]);
        this.jitter = Integer.parseInt(fields[startIndex]);
    }

    public int getCpuUsage() {
        return cpuUsage;
    }

    public int getRamUsage() {
        return ramUsage;
    }

    public int getInterfaceStats() {
        return interfaceStats;
    }

    public int getPacketLoss() {
        return packetLoss;
    }

    public int getJitter() {
        return jitter;
    }

    public String toString() {
        return "cpu: " + cpuUsage + ", ram: " + ramUsage + ", interface: " + interfaceStats + ", packet: " + packetLoss + ", jitter: " + jitter;
    }

    public String getPayload(){
        return cpuUsage + ";" +
                ramUsage + ";" +
                interfaceStats + ";" +
                packetLoss + ";" +
                jitter;
    }
}
