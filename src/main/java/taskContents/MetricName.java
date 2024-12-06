package taskContents;

public enum MetricName {
    CPU_USAGE,
    RAM_USAGE,
    INTERFACE_STATS,
    LATENCY,
    PACKET_LOSS,
    JITTER,
    BANDWIDTH;

    public static MetricName fromInteger(int x){
        return switch (x) {
            case 0 -> CPU_USAGE;
            case 1 -> RAM_USAGE;
            case 2 -> INTERFACE_STATS;
            case 3 -> LATENCY;
            case 4 -> PACKET_LOSS;
            case 5 -> JITTER;
            case 6 -> BANDWIDTH;
            default -> null;
        };
    }

    public int toInteger(){
        return switch (this) {
            case CPU_USAGE -> 0;
            case RAM_USAGE -> 1;
            case INTERFACE_STATS -> 2;
            case LATENCY -> 3;
            case PACKET_LOSS -> 4;
            case JITTER -> 5;
            case BANDWIDTH -> 6;
        };
    }

    @Override
    public String toString() {
        return switch (this){
            case LATENCY -> "Latency";
            case PACKET_LOSS -> "Packet Loss";
            case JITTER -> "Jitter";
            case CPU_USAGE -> "CPU Usage";
            case RAM_USAGE -> "RAM Usage";
            case INTERFACE_STATS -> "Interface Stats";
            case BANDWIDTH -> "Bandwidth";
        };
    }
}
