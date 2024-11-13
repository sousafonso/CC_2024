/**
 * LocalMetric.java
 * @description: Classe que representa uma métrica local, contendo o nome da métrica, o número de interfaces e a lista de interfaces.
 * @responsibility:
 * Armazenar o nome da métrica, o número de interfaces e a lista de interfaces.
 * Gerar uma string com o conteúdo da métrica.
 */

// package taskContents;

// import java.util.List;

// public class LocalMetric {
//     private MetricName metricName; 
//     private int numInterfaces;
//     private List<String> interfaces;

//     public LocalMetric(MetricName metricName, List<String> interfaces) {
//         this.metricName = metricName;
//         if(interfaces != null) {
//             this.numInterfaces = interfaces.size();
//         }
//         else{
//             this.numInterfaces = 0;
//         }
//         this.interfaces = interfaces;
//     }

//     public String getPayload(){
//         String s = metricName.toInteger() + ";" +
//                 numInterfaces;

//         if(numInterfaces > 0){
//             for (String i : interfaces) {
//                 s += ";" + i;
//             }
//         }

//         return s;
//     }

//      public String toString(){
//         String interfacesStr = "";
//         for(String i : this.interfaces){
//             interfacesStr += i + " ";
//         }
//         return "nome= " + this.metricName.name() + ", numInterfaces= " + this.numInterfaces + "interfaces= " + interfacesStr;
//     }
// }

package taskContents;

import java.util.List;

public class LocalMetric {
    private MetricName metricName;
    private int numInterfaces;
    private List<String> interfaces;

    public LocalMetric(MetricName metricName, List<String> interfaces) {
        this.metricName = metricName;
        this.numInterfaces = interfaces != null ? interfaces.size() : 0;
        this.interfaces = interfaces;
    }

    public String getPayload() {
        StringBuilder payload = new StringBuilder();
        payload.append(metricName.toInteger()).append(";").append(numInterfaces);
        for (String iface : interfaces) {
            payload.append(";").append(iface);
        }
        return payload.toString();
    }

    @Override
    public String toString() {
        return "name= " + metricName.name() + ", numInterfaces= " + numInterfaces + ", interfaces= " + interfaces;
    }
}
