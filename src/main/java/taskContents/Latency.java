package taskContents;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

public class Latency extends LinkMetric{
    private int frequency;
    private int packageQuantity;

    public Latency(MetricName name, String destination, int frequency, int packageQuantity) {
        super(name, destination);
        this.frequency = frequency;
        this.packageQuantity = packageQuantity;
    }

    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeInt(super.getMetricName().toInteger());
            dos.writeInt(this.frequency);
            dos.writeInt(this.packageQuantity);
            dos.writeChars(super.getDestination());
            dos.flush();
        } catch (IOException e) {
            System.out.println("Erro ao serializar objeto");
        }

        return out.toByteArray();
    }

    public String toString(){
        return "name= " + super.getMetricName().name() + ", destination" + super.getDestination() +", frequency= " + frequency + ", packageQuantity= " + packageQuantity;
    }
}
