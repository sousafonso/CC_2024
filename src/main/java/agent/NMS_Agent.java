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

import message.Message;
import message.MessageType;
import message.TaskResult;
import taskContents.LocalMetric;
import taskContents.MetricName;

public class NMS_Agent {
    private final String SERVER_HOST_NAME = "127.0.0.1"; //TODO mudar conforme topologia
    private final int SERVER_UDP_PORT = 5000;
    // private final int SERVER_TCP_PORT = 6000;
    private InetAddress serverIP;

    private String agentId;
    private MetricCollector metricCollector;
    private LocalMetric localMetric;
    // private AlertFlowClient alertFlowClient;
    private NetTaskClient netTaskClient;

    // Limites para disparo de alertas
    // private final double CPU_USAGE_LIMIT = 80.0;
    // private final double RAM_USAGE_LIMIT = 90.0;
    // private final int INTERFACE_PPS_LIMIT = 2000;
    // private final double PACKET_LOSS_LIMIT = 5.0;
    // private final int JITTER_LIMIT = 100;

    // public NMS_Agent(String agentId) {
    //     this.agentId = agentId;
    //     try {
    //         this.serverIP = InetAddress.getByName(SERVER_HOST_NAME);
    //     } catch (UnknownHostException e) {
    //         System.err.println("ERROR: Could not resolve server hostname: " + SERVER_HOST_NAME);
    //     }
    // }

    public NMS_Agent(String agentId) {
        this.agentId = agentId;
        this.localMetric = new LocalMetric(MetricName.CPU_USAGE, null); // Exemplo de inicialização
        try {
            this.serverIP = InetAddress.getByName(SERVER_HOST_NAME);
        } catch (UnknownHostException e) {
            System.err.println("ERROR: Could not resolve server hostname: " + SERVER_HOST_NAME);
        }
    }

    // private void start() {
    //     Thread NetTaskClient = new Thread(new NetTaskClient(serverIP, SERVER_UDP_PORT));
    //     NetTaskClient.start();
    // }

    private void start() {
        netTaskClient = new NetTaskClient(serverIP, SERVER_UDP_PORT);
        new Thread(netTaskClient).start();
        startMetricCollection();
        // startAlertMonitoring();
    }

    private void startMetricCollection() {
        new Thread(() -> {
            while (true) {
                try {
                    double cpuUsage = localMetric.collectCpuUsage();
                    double ramUsage = localMetric.collectRamUsage();
                    String interfaceStats = localMetric.collectInterfaceStats();

                    // Enviar métricas coletadas
                    TaskResult result = new TaskResult(agentId, MetricName.CPU_USAGE, String.valueOf(cpuUsage));
                    Message message = new Message(1, 0, MessageType.TaskResult, result);
                    netTaskClient.sendMessage(message);

                    result = new TaskResult(agentId, MetricName.RAM_USAGE, String.valueOf(ramUsage));
                    message = new Message(1, 0, MessageType.TaskResult, result);
                    netTaskClient.sendMessage(message);

                    result = new TaskResult(agentId, MetricName.INTERFACE_STATS, interfaceStats);
                    message = new Message(1, 0, MessageType.TaskResult, result);
                    netTaskClient.sendMessage(message);

                    Thread.sleep(5000); // Coleta a cada 5 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                    Data metricData = metricCollector.collectPing("8.8.8.8"); // Exemplo de coleta de ping
                    TaskResult result = new TaskResult(agentId, MetricName.LATENCY, metricData.getPayload());
                    Message message = new Message(1, 0, MessageType.TaskResult, result);
                    netTaskClient.sendMessage(message);
                    Thread.sleep(5000); // Coleta a cada 5 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // APENAS UM EXEMPLO
    private void checkCriticalConditions() {
        // Exemplo de coleta de métricas
        double cpuUsage = metricCollector.collectCpuUsage();
        double ramUsage = metricCollector.collectRamUsage();
        int interfacePps = metricCollector.collectInterfacePps();
        double packetLoss = metricCollector.collectPacketLoss();
        int jitter = metricCollector.collectJitter();

        if (cpuUsage > CPU_USAGE_LIMIT) {
            sendAlert(new Notification(agentId, "CPU usage exceeded: " + cpuUsage + "%"));
        }
        if (ramUsage > RAM_USAGE_LIMIT) {
            sendAlert(new Notification(agentId, "RAM usage exceeded: " + ramUsage + "%"));
        }
        if (interfacePps > INTERFACE_PPS_LIMIT) {
            sendAlert(new Notification(agentId, "Interface PPS exceeded: " + interfacePps + " pps"));
        }
        if (packetLoss > PACKET_LOSS_LIMIT) {
            sendAlert(new Notification(agentId, "Packet loss exceeded: " + packetLoss + "%"));
        }
        if (jitter > JITTER_LIMIT) {
            sendAlert(new Notification(agentId, "Jitter exceeded: " + jitter + " ms"));
        }
    }

    private void sendAlert(Notification notification) {
        try (Socket socket = new Socket(serverIP, SERVER_TCP_PORT);
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {
            writer.println("Alerta: " + notification.getNotificationId() + " - " + notification.getMessage());
            System.out.println("Alerta enviado via TCP: " + notification.getNotificationId());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
