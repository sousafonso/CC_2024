package taskContents;

public enum MetricName {
    LATENCY,
    PACKET_LOSS,
    JITTER,
    CPU_USAGE,
    RAM_USAGE,
    INTERFACE_STATS;

    public MetricName fromInteger(int x){
        return switch (x) {
            case 0 -> LATENCY;
            case 1 -> PACKET_LOSS;
            case 2 -> JITTER;
            case 3 -> CPU_USAGE;
            case 4 -> RAM_USAGE;
            case 5 -> INTERFACE_STATS;
            default -> null;
        };
    }

    public int toInteger(){
        return switch (this) {
            case LATENCY -> 0;
            case PACKET_LOSS -> 1;
            case JITTER -> 2;
            case CPU_USAGE -> 3;
            case RAM_USAGE -> 4;
            case INTERFACE_STATS -> 5;
        };
    }
}
