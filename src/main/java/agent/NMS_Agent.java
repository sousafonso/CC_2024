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

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import message.*;
import taskContents.Conditions;
import taskContents.MetricName;

public class NMS_Agent {
    private final String SERVER_HOST_NAME = "127.0.0.1"; //TODO mudar conforme topologia
    private final int SERVER_UDP_PORT = 5000;
    private final int SERVER_TCP_PORT = 6000;
    private final int UDP_PORT = 7777;

    private Lock alertValuesLock = new ReentrantLock();
    private InetAddress serverIP;
    private Map<MetricName, Integer> alertValues;
    private Task task;
    private String agentId;

    public NMS_Agent(String agentId) {
        this.agentId = agentId;
        try {
            this.serverIP = InetAddress.getByName(SERVER_HOST_NAME);
        } catch (UnknownHostException e) {
            System.err.println("ERROR: Could not resolve server hostname: " + SERVER_HOST_NAME);
        }
        this.alertValues = new HashMap<>();
    }

    private void processConditions(Conditions conditions){
        alertValuesLock.lock();
        int cpuUsage = conditions.getCpuUsage();
        if(cpuUsage >= 0){
            alertValues.put(MetricName.CPU_USAGE, cpuUsage);
        }

        int ramUsage = conditions.getRamUsage();
        if(ramUsage >= 0){
            alertValues.put(MetricName.RAM_USAGE, ramUsage);
        }

        int interfaceStats = conditions.getInterfaceStats();
        if(interfaceStats >= 0){
            alertValues.put(MetricName.INTERFACE_STATS, interfaceStats);
        }

        int packetLoss = conditions.getPacketLoss();
        if(packetLoss >= 0){
            alertValues.put(MetricName.PACKET_LOSS, packetLoss);
        }

        int jitter = conditions.getJitter();
        if(jitter >= 0){
            alertValues.put(MetricName.JITTER, jitter);
        }
        alertValuesLock.unlock();
    }

    private void processTask(){
    }

    private void start() {
        DatagramSocket socket = null;
        try{
            Random random = new Random();
            socket = new DatagramSocket(UDP_PORT);
            int seqNumber = random.nextInt(Integer.MAX_VALUE);
            byte[] byteMsg = (new Message(seqNumber, 0, MessageType.Regist, null)).getPDU();
            DatagramPacket sendPacket = new DatagramPacket(byteMsg, byteMsg.length, serverIP, SERVER_UDP_PORT);
            socket.send(sendPacket);

            byte[] receiveMsg = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveMsg, receiveMsg.length);
            //TODO timeout, se nao receber dentro do tempo enviar novamente o registo
            socket.receive(receivePacket);
            Message msg = new Message(receivePacket.getData());

            byteMsg = (new Message(msg.getSeqNumber() + 1, msg.getSeqNumber(), MessageType.Ack, null)).getPDU();
            sendPacket = new DatagramPacket(byteMsg, byteMsg.length);
            socket.send(sendPacket);

            this.task = (Task) msg.getData();
            System.out.println("Received task: " + task);

            processConditions(this.task.getConditions());
            System.out.println("Received conditions: " + this.alertValues);

        }
        catch(SocketException e){
            System.out.println("UDP Socket Agent Error");
        }
        catch (IOException e){
            System.out.println("Erro ao enviar pacote");
        }
        finally{
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        }
    }


    public static void main(String[] args) {
        NMS_Agent agent = new NMS_Agent(args[0]);
        agent.start();
    }

    /*
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
