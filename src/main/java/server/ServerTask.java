package server;

import message.Task;

public class ServerTask {
    private Task task;
    private int cpuUsage;
    private int ramUsage;
    private int interfaceStats; // limite de pacotes opr segundo para as interfaces (limite de 2000 pacotes por segundo, por exemplo)
    private int packetLoss;
    private int jitter;

    public ServerTask(Task task) {
        this.task = task;
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
}
