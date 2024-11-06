package server;

public class AlertFlowConditions {
    private int ramUsage;
    private int interfaceStats; // limite de pacotes opr segundo para as interfaces (limite de 2000 pacotes por segundo, por exemplo)
    private int packetLoss;
    private int jitter;
    private int cpuUsage;

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

    public int getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(int cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public boolean isAlert() {
        return ramUsage > 90 || interfaceStats > 2000 || packetLoss > 5 || jitter > 10 || cpuUsage > 90;
    }

    public String getAlertMessage() {
        if (ramUsage > 90) {
            return "Uso de memória RAM acima de 90%";
        } else if (interfaceStats > 100) {
            return "Pacotes por segundo acima de 2000";
        } else if (packetLoss > 5) {
            return "Perda de pacotes acima de 5%";
        } else if (jitter > 10) {
            return "Jitter acima de 10ms";
        } else if (cpuUsage > 90) {
            return "Uso de CPU acima de 90%";
        } else {
            return "Sem alertas";
        }
    }

    @Override
    public String toString() {
        return "AlertFlowConditions{" +
                "ramUsage=" + ramUsage +
                ", interfaceStats=" + interfaceStats +
                ", packetLoss=" + packetLoss +
                ", jitter=" + jitter +
                ", cpuUsage=" + cpuUsage +
                '}';
    }
}