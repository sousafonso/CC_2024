/**
 * Task.java
 * @description Classe que representa uma tarefa a ser executada por um NMS_Agent. A tarefa é composta por um
 * identificador, tipo, descrição, valor, timestamp, frequência e métricas.
 */

package message;

import java.util.ArrayList;
import java.util.List;

import taskContents.*;

public class Task extends Data {
    private String id; 
    private int frequency;
    private Conditions conditions;
    private int numLinkMetrics;
    private int numLocalMetrics;
    private List<LinkMetric> linkMetrics;
    private List<LocalMetric> localMetrics;

    public Task(String timestamp, String id, int frequency, List<LocalMetric> localMetrics, List<LinkMetric> linkMetrics) {
        //super(timestamp);
        this.id = id;
        this.frequency = frequency;
        if (localMetrics != null) {
            this.numLinkMetrics = linkMetrics.size();
        }
        else {
            this.numLinkMetrics = 0;
        }

        if (localMetrics != null) {
            this.numLocalMetrics = localMetrics.size();
        }
        else {
            this.numLocalMetrics = 0;
        }

        this.localMetrics = localMetrics;
        this.linkMetrics = linkMetrics;
    }

    public Task(String[] fields, int startIndex){
        this.id = fields[startIndex++];
        this.frequency = Integer.parseInt(fields[startIndex++]);
        this.conditions = new Conditions(fields, startIndex);
        this.numLinkMetrics = Integer.parseInt(fields[startIndex++]);
        this.numLocalMetrics = Integer.parseInt(fields[startIndex++]);

        if(numLinkMetrics > 0){
            this.linkMetrics = new ArrayList<>(this.numLinkMetrics);

            for(int i = 0; i < numLinkMetrics; i++){
                MetricName name = MetricName.fromInteger(Integer.parseInt(fields[startIndex++]));
                String destination = fields[startIndex++];

                if(name == MetricName.LATENCY) {
                    int frequency = Integer.parseInt(fields[startIndex++]);
                    int packageQuantity = Integer.parseInt(fields[startIndex++]);
                    Latency latency = new Latency(name, destination, frequency, packageQuantity);
                    this.linkMetrics.add(latency);
                }
                else{
                    char role = fields[startIndex++].charAt(0);
                    int duration = Integer.parseInt(fields[startIndex++]);
                    String protocol = fields[startIndex++];
                    IperfMetric iperfMetric = new IperfMetric(name, destination, role, duration, protocol);
                    this.linkMetrics.add(iperfMetric);
                }
            }
        }
        else{
            this.linkMetrics = null;
        }

        if(numLocalMetrics > 0){
            this.localMetrics = new ArrayList<>(this.numLocalMetrics);

            for(int i = 0; i < numLocalMetrics; i++){
                MetricName name = MetricName.fromInteger(Integer.parseInt(fields[startIndex++]));
                int nInterfaces = Integer.parseInt(fields[startIndex++]);
                List<String> interfaces;
                if(nInterfaces > 0){
                    interfaces = new ArrayList<>(nInterfaces);
                    for(int j = 0; j < nInterfaces; j++){
                        interfaces.add(fields[startIndex++]);
                    }
                }
                else{
                    interfaces = null;
                }

                LocalMetric localMetric = new LocalMetric(name, interfaces);
                this.localMetrics.add(localMetric);
            }
        }
        else{
            this.localMetrics = null;
        }
    }

    public String getId() {
        return id;
    }

    @Override
    public String getPayload() {
        StringBuilder s = new StringBuilder(id + ";" +
                frequency + ";" +
                conditions.getPayload() + ";" +
                numLinkMetrics + ";" +
                numLocalMetrics);

        if(this.numLinkMetrics > 0) {
            for (LinkMetric lm : this.linkMetrics) {
                s.append(";").append(lm.getPayload());
            }
        }
        if(this.numLocalMetrics > 0) {
            for (LocalMetric lm : this.localMetrics) {
                s.append(";").append(lm.getPayload());
            }
        }

        return s.toString();
    }

    @Override
    public String toString() {
        String linkMetricsStr = "";
        String localMetricsStr = "";

        for(LinkMetric lm : this.linkMetrics) {
            linkMetricsStr += lm.toString();
            linkMetricsStr += " ";
        }

        for(LocalMetric lm : this.localMetrics) {
            localMetricsStr += lm.toString();
            localMetricsStr += " ";
        }

        return "Task{" +
                //"timestamp='" + super.getTimestamp() + '\'' +
                "id='" + getId() + '\'' +
                ", frequency='" + frequency + '\'' +
                ", numLinkMetrics=" + numLinkMetrics +
                ", numLocalMetrics=" + numLocalMetrics +
                ", LinkMetrics=" + linkMetricsStr +
                ", localMetrics=" + localMetricsStr +
                '}';
    }
}
