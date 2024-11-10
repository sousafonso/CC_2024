package taskContents;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

public class IperfMetric extends LinkMetric{
    private char role;
    private int duration;
    private String protocol;

    public IperfMetric(MetricName name, String destination, char role, int duration, String protocol) {
        super(name, destination);
        this.role = role;
        this.duration = duration;
        this.protocol = protocol;
    }

    public byte[] getPayload() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeInt(super.getMetricName().toInteger());
            dos.writeChar(this.role);
            dos.writeInt(this.duration);
            dos.writeChars(this.protocol);
            dos.writeChars(super.getDestination());
            dos.flush();
        } catch (IOException e) {
            System.out.println("Erro ao serializar objeto");
        }

        return out.toByteArray();
    }

    public String toString() {
        return "name= " + super.getMetricName().name() + ", destination" + super.getDestination() + ", role= " + role + ", duration= " + duration + ", protocol= " + protocol;
    }
}
