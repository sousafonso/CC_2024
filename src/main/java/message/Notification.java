// package message;

// public class Notification extends Data {
//     private Conditions conditions;

//     public Notification(int cpuUsage, int ramUsage, int interfaceStats, int packetLoss, int jitter) {
//         this.conditions = new Conditions(cpuUsage, ramUsage, interfaceStats, packetLoss, jitter);
//     }

//     public Notification(String[] fields, int startIndex){
//         this.conditions = new Conditions(fields, startIndex);
//     }

//     public int getCpuUsage() {
//         return conditions.getCpuUsage();
//     }

//     public int getRamUsage() {
//         return conditions.getRamUsage();
//     }

//     public int getInterfaceStats() {
//         return conditions.getInterfaceStats();
//     }

//     public int getPacketLoss() {
//         return conditions.getPacketLoss();
//     }

//     public int getJitter() {
//         return conditions.getJitter();
//     }

//     public String getPayload() {
//         return this.conditions.getPayload();
//     }
// }

package message;

public class Notification extends Data {
    //private String agentId;
    private String alertType;
    private double alertValue;
    private double threshold;
    private String timestamp;

    public Notification(String agentId, String alertType, double alertValue, double threshold, String timestamp) {
        //this.agentId = agentId;
        this.alertType = alertType;
        this.alertValue = alertValue;
        this.threshold = threshold;
        this.timestamp = timestamp;
    }

    public Notification(String[] fields, int startIndex){
        //this.agentId = fields[startIndex++];
        this.alertType = fields[startIndex++];
        this.alertValue = Double.parseDouble(fields[startIndex++]);
        this.threshold = Double.parseDouble(fields[startIndex++]);
        this.timestamp = fields[startIndex];
    }

    public String getNotificationId() {
        return alertType + "-" + timestamp;
    }

    public String getMessage() {
        return "Alert: " + alertType + " exceeded threshold with value " + alertValue;
    }

    @Override
    public String getPayload() {
        return alertType + ";" + alertValue + ";" + threshold + ";" + timestamp;
    }
}
