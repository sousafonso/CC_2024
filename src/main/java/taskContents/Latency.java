package taskContents;

public class Latency extends LinkMetric{
    private int frequency; // frequencia de envio de pacotes
    private int packageQuantity; // quantidade de pacotes enviados

    public Latency(MetricName name, String destination, int frequency, int packageQuantity) {
        super(name, destination);
        this.frequency = frequency;
        this.packageQuantity = packageQuantity;
    }

    @Override
    public String getPayload() {
        return super.getMetricName().toInteger() + ";" +
                super.getDestination() + ";" +
                frequency + ";" +
                packageQuantity;
    }

    public String toString(){
        return "name= " + super.getMetricName().name() + ", destination" + super.getDestination() +", frequency= " + frequency + ", packageQuantity= " + packageQuantity;
    }
}
