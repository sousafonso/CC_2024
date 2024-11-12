/**
 * @description: Classe principal do agente, responsável por se registar no servidor, recolher métricas e enviar alertas.
 * Envia mensagens para o NetTaskHandler e o AlertFlowHandler no servidor através de NetTaskClient e AlertFlowClient.
 * 
 * @responsibility: 
 *  Registar o agente no servidor ao iniciar.
    Recolher métricas periodicamente e enviá-las ao servidor.
    Monitorizar condições críticas e enviar alertas quando necessário.
 */

package agent;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NMS_Agent {
    private final String SERVER_HOST_NAME = "127.0.0.1"; //TODO mudar conforme topologia
    private final int SERVER_UDP_PORT = 5000;
    private final int SERVER_TCP_PORT = 6000;
    private InetAddress serverIP;

    private String agentId;
    /*private MetricCollector metricCollector;
    private NetTaskClient netTaskClient;
    private AlertFlowClient alertFlowClient;*/

    public NMS_Agent(String agentId) {
        this.agentId = agentId;
        try {
            this.serverIP = InetAddress.getByName(SERVER_HOST_NAME);
        } catch (UnknownHostException e) {
            System.err.println("ERROR: Could not resolve server hostname: " + SERVER_HOST_NAME);
        }
    }

    private void start() {
        Thread NetTaskClient = new Thread(new NetTaskClient(serverIP, SERVER_UDP_PORT));
        NetTaskClient.start();
    }

    public static void main(String[] args) {
        NMS_Agent agent = new NMS_Agent(args[0]);
        agent.start();
    }

    /*@Override
    public void run() {
        System.out.println("Agente " + agentId + " iniciado.");
        startMetricCollection();
        startAlertMonitoring();
    }

    private void startMetricCollection() {
        new Thread(() -> {
            while (true) {
                try {
                    Data metricData = metricCollector.collectPing("8.8.8.8"); // Exemplo de recolha de ping
                    TaskResult result = new TaskResult(agentId, true, metricData.getValue().toString());
                    netTaskClient.sendTaskResult(result);
                    Thread.sleep(5000); // Recolha a cada 5 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startAlertMonitoring() {
        new Thread(() -> {
            while (true) {
                if (checkCriticalConditions()) {
                    Notification alert = new Notification(agentId, "Critical condition met!");
                    alertFlowClient.sendAlert(alert);
                }
                try {
                    Thread.sleep(10000); // Checa condições a cada 10 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/
}
