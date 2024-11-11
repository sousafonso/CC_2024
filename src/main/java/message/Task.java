/**
 * Task.java
 * @description Classe que representa uma tarefa a ser executada por um NMS_Agent. A tarefa é composta por um
 * identificador, tipo, descrição, valor, timestamp, frequência e métricas.
 */

package message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import agent.Device;
import taskContents.LinkMetric;
import taskContents.LocalMetric;

public class Task extends Data {
    private String id; 
    private int frequency;
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

        if (linkMetrics != null) {
            this.numLocalMetrics = localMetrics.size();
        }
        else {
            this.numLocalMetrics = 0;
        }

        this.localMetrics = localMetrics;
        this.linkMetrics = linkMetrics;
    }

    public Task(){
        this.id = "";
        this.frequency = 0;
        this.numLinkMetrics = 0;
        this.numLocalMetrics = 0;
        this.linkMetrics = null;
        this.localMetrics = null;
    }

    public String getId() {
        return id;
    }

    @Override
    public Data rebuild(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedMessage);
        try{
            DataInputStream dis = new DataInputStream(bais);
            byte[] stringId = new byte[ID_SIZE];
            dis.readFully(stringId);
            this.id = new String(stringId, StandardCharsets.UTF_16LE);
            this.frequency = dis.readInt();
            this.numLinkMetrics = dis.readInt();
            this.numLocalMetrics = dis.readInt();

        } catch (IOException e) {
            System.out.println("Mensagem no formato errado");
        }
    }

    @Override
    public String getPayload() {
        StringBuilder s = new StringBuilder(id + ";" +
                frequency + ";" +
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
