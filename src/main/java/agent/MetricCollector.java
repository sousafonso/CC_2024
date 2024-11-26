package agent;

import taskContents.LinkMetric;
import taskContents.LocalMetric;

public class MetricCollector implements Runnable {
    int frequency;
    LocalMetric localMetric;
    LinkMetric linkMetric;

    public MetricCollector(int frequency, LocalMetric localMetric, LinkMetric linkMetric) {
        this.frequency = frequency;
        this.localMetric = localMetric;
        this.linkMetric = linkMetric;
    }

    public void processLocalMetric(){
        // a cada frequency tempo:
        // processar tarefa
        // switch para cada tipo de tarefa
        // pegar no resultado
        // mandar resultado
    }

    public void processLinkMetric(){

    }

    public void run() {
        if (localMetric != null) {
            processLocalMetric();
        }
        else{
            processLinkMetric();
        }
    }
}