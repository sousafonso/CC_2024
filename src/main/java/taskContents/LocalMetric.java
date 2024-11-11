package taskContents;

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

    public String getPayload(){
        String s = metricName.toInteger() + ";" +
                numInterfaces;

        if(numInterfaces > 0){
            for (String i : interfaces) {
                s += ";" + i;
            }
        }

        return s;
    }

     public String toString(){
        String interfacesStr = "";
        for(String i : this.interfaces){
            interfacesStr += i + " ";
        }
        return "nome= " + this.metricName.name() + ", numInterfaces= " + this.numInterfaces + "interfaces= " + interfacesStr;
    }
}
