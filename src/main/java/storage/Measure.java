package storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Measure {
    private double value;
    LocalDateTime timestamp;
    String measureInterface;

    public Measure(double value, LocalDateTime timestamp, String measureInterface) {
        this.value = value;
        this.timestamp = timestamp;
        this.measureInterface = measureInterface;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(this.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("]");
        if(!this.measureInterface.isEmpty()){
            sb.append(" (").append(this.measureInterface).append(")");
        }
        sb.append(" Valor: ").append(this.value);
        return sb.toString();
    }
}
