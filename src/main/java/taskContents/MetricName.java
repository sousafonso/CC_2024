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
            case 0 -> LATENCY;
            case 1 -> PACKET_LOSS;
            case 2 -> JITTER;
            case 3 -> CPU_USAGE;
            case 4 -> RAM_USAGE;
            case 5 -> INTERFACE_STATS;
            case 6 -> BANDWIDTH;
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
            case BANDWIDTH -> 6;
        };
    }

    @Override
    public String toString() {
        return switch (this){
            case LATENCY -> "Latência";
            case PACKET_LOSS -> "Perda de pacotes";
            case JITTER -> "Jitter";
            case CPU_USAGE -> "Utilização do CPU";
            case RAM_USAGE -> "Utilização da RAM";
            case INTERFACE_STATS -> "Stats da interface";
            case BANDWIDTH -> "Largura de banda";
        };
    }
}
