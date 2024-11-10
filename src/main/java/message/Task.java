/**
 * Task.java
 * @description Classe que representa uma tarefa a ser executada por um NMS_Agent. A tarefa é composta por um
 * identificador, tipo, descrição, valor, timestamp, frequência e métricas.
 */

package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
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

    public String getId() {
        return id;
    }

    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            DataOutputStream dos = new DataOutputStream(out);
            //dos.writeChars(super.getTimestamp());
            dos.writeChars(this.id);
            dos.writeInt(this.frequency);
            dos.writeInt(this.numLinkMetrics);
            dos.writeInt(this.numLocalMetrics);
            if(this.numLinkMetrics > 0) {
                for (LinkMetric lm : this.linkMetrics) {
                    byte[] data = lm.getPayload();
                    dos.write(data, 0, data.length);
                }
            }
            if(this.numLocalMetrics > 0) {
                for (LocalMetric lm : this.localMetrics) {
                    byte[] data = lm.getPayload();
                    dos.write(data, 0, data.length);
                }
            }
            dos.flush();
        } catch (IOException e) {
            System.out.println("Erro ao serializar objeto");
        }

        return out.toByteArray();
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
