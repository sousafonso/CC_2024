package taskContents;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.List;

public class LocalMetric {
    private MetricName metricName;
    private int numInterfaces;
    private List<String> interfaces;

    public LocalMetric(MetricName metricName, List<String> interfaces) {
        this.metricName = metricName;
        if(interfaces != null) {
            this.numInterfaces = interfaces.size();
        }
        else{
            this.numInterfaces = 0;
        }
        this.interfaces = interfaces;
    }

    public byte[] getPayload(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeInt(this.metricName.toInteger());
            dos.writeInt(this.numInterfaces);
            if(this.numInterfaces > 0) {
                for (String i : this.interfaces) {
                    dos.writeChars(i);
                }
            }
            dos.flush();
        } catch (IOException e) {
            System.out.println("Erro ao serializar objeto");
        }

        return out.toByteArray();
    }

     public String toString(){
        String interfacesStr = "";
        for(String i : this.interfaces){
            interfacesStr += i + " ";
        }
        return "nome= " + this.metricName.name() + ", numInterfaces= " + this.numInterfaces + "interfaces= " + interfacesStr;
    }
}
