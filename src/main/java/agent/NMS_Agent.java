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

import message.*;
import taskContents.*;

import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NMS_Agent {
    private final int MAX_RETRIES = 5;
    private final int TIMEOUT_MILLIS = 3000;
    private final Duration TIMEOUT = Duration.ofMillis(TIMEOUT_MILLIS);
    private Connection connection;
    private Map<MetricName, Integer> alertValues;
    private static ConcurrentHashMap<Integer, ResultStatus> waitingAck = new ConcurrentHashMap<>();
    private Task task;

    public NMS_Agent(String agentHostName) {
        this.connection = new Connection(TIMEOUT_MILLIS, agentHostName);
        this.alertValues = new HashMap<>();
    }

    public static void addToAckWaitingList(LocalDateTime timeSent, Message taskResult) {
        waitingAck.put(taskResult.getSeqNumber(), new ResultStatus(timeSent, taskResult));
    }

    private void processConditions(Conditions conditions) {
        int cpuUsage = conditions.getCpuUsage();
        if (cpuUsage >= 0) {
            alertValues.put(MetricName.CPU_USAGE, cpuUsage);
        }

        int ramUsage = conditions.getRamUsage();
        if (ramUsage >= 0) {
            alertValues.put(MetricName.RAM_USAGE, ramUsage);
        }

        int interfaceStats = conditions.getInterfaceStats();
        if (interfaceStats >= 0) {
            alertValues.put(MetricName.INTERFACE_STATS, interfaceStats);
        }

        int packetLoss = conditions.getPacketLoss();
        if (packetLoss >= 0) {
            alertValues.put(MetricName.PACKET_LOSS, packetLoss);
        }

        int jitter = conditions.getJitter();
        if (jitter >= 0) {
            alertValues.put(MetricName.JITTER, jitter);
        }
    }

    private void processTask() {
        int frequency = this.task.getFrequency();
        List<LocalMetric> localMetrics = this.task.getLocalMetrics();
        List<LinkMetric> linkMetrics = this.task.getLinkMetrics();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.min(this.task.getNumLinkMetrics() + this.task.getNumLocalMetrics(), Runtime.getRuntime().availableProcessors()));

        for (LocalMetric localMetric : localMetrics) {
            executor.scheduleAtFixedRate(new MetricCollector(connection, this.task.getId(), alertValues.getOrDefault(localMetric.getMetricName(), -1), localMetric, null), 0, frequency, TimeUnit.SECONDS);
        }

        for (LinkMetric linkMetric : linkMetrics) {
            if ((linkMetric instanceof IperfMetric) && ((IperfMetric) linkMetric).getRole() == 's') {
                new Thread(new MetricCollector(connection, this.task.getId(), alertValues.getOrDefault(linkMetric.getMetricName(), -1), null, linkMetric)).start();
            } else {
                executor.scheduleAtFixedRate(new MetricCollector(connection, this.task.getId(), alertValues.getOrDefault(linkMetric.getMetricName(), -1), null, linkMetric), 0, frequency, TimeUnit.SECONDS);
            }
        }
    }

    private Message registerAgent() {
        Message msg = null;
        try {
            // Enviar pacote de registo ao servidor
            int seqNumber = new Random().nextInt(Integer.MAX_VALUE);
            byte[] byteMsg = (new Message(seqNumber, 0, MessageType.Regist, null)).getPDU();
            System.out.println("[ENVIO] Registo para o servidor");
            connection.sendViaUDP(byteMsg);

            // Esperar TIMEOUT, se não receber a tarefa (confirmação), enviar novamente o registo
            boolean waiting = true;
            for (int i = 0; waiting && i < MAX_RETRIES; i++) {
                try {
                    msg = connection.receiveViaUDP();
                    waiting = false;
                } catch (SocketTimeoutException e) {
                    System.out.println("[RE-ENVIO] Registo para o servidor");
                    connection.sendViaUDP(byteMsg);
                }
            }

            if (waiting){
                System.out.println("Tentativas de registo no servidor excedidas");
                return null;
            }

            if(msg.getType() == MessageType.Task) {
                System.out.println("[RECEBIDO] Tarefa do servidor");
                byteMsg = (new Message(msg.getSeqNumber() + 1, msg.getSeqNumber(), MessageType.Ack, null)).getPDU();
                System.out.println("[ENVIO] ACK " + msg.getSeqNumber() + " para o servidor");
                connection.sendViaUDP(byteMsg);
            }
        } catch (Exception e) {
            System.out.println("Erro ao enviar pacote de registo");
        }

        return msg;
    }

    private void start() {
        try {
            Message msg = registerAgent();
            if (msg == null) {
                System.out.println("Erro ao registar agente no servidor");
                return;
            }

            this.task = (Task) msg.getData();

            if(this.task == null || msg.getType() != MessageType.Task){
                System.out.println("Erro ao receber tarefa do servidor");
                return;
            }

            processConditions(this.task.getConditions());
            processTask();

            new Thread(() -> {
                while (true) {
                    try {
                        Message ack = connection.receiveViaUDP();
                        if (ack.getType() == MessageType.Ack) {
                            System.out.println("[RECEBIDO] ACK do servidor");
                            waitingAck.remove(ack.getAckNumber());
                        }
                    } catch (SocketTimeoutException ignored) {}
                }
            }).start();

            while (true) {
                for(ResultStatus result : waitingAck.values()) {
                    if (result.getTries() > MAX_RETRIES) {
                        waitingAck.remove(result.getMessage().getSeqNumber());
                        continue;
                    }

                    Duration timeDifference = Duration.between(result.getTimeSent(), LocalDateTime.now());
                    if (timeDifference.compareTo(TIMEOUT) > 0) {
                        System.out.println("[RE-ENVIO] Resultado para servidor");
                        connection.sendViaUDP(result.getMessage().getPDU());
                        result.setTimeSent(LocalDateTime.now());
                        result.incTries();
                        waitingAck.put(result.getMessage().getSeqNumber(), result);
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }

            }
        } finally {
            connection.close();
        }
    }

    public static void main(String[] args) {
        NMS_Agent agent = new NMS_Agent(args[0]);
        agent.start();
    }
}
