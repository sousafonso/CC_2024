package message;

public class Notification extends Data {
    private Conditions conditions;

    public Notification(int cpuUsage, int ramUsage, int interfaceStats, int packetLoss, int jitter) {
        this.conditions = new Conditions(cpuUsage, ramUsage, interfaceStats, packetLoss, jitter);
    }

    public Notification(String[] fields, int startIndex){
        this.conditions = new Conditions(fields, startIndex);
    }

    public int getCpuUsage() {
        return conditions.getCpuUsage();
    }

    public int getRamUsage() {
        return conditions.getRamUsage();
    }

    public int getInterfaceStats() {
        return conditions.getInterfaceStats();
    }

    public int getPacketLoss() {
        return conditions.getPacketLoss();
    }

    public int getJitter() {
        return conditions.getJitter();
    }

    public String getPayload() {
        return this.conditions.getPayload();
    }
}
