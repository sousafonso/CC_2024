package storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Measure {
    private double value;
    LocalDateTime timestamp;

    public Measure(double value, LocalDateTime timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "[" + this.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "] " + "Valor: " + this.value;
    }
}
