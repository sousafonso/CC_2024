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
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import message.*;
import taskContents.Conditions;
import taskContents.LinkMetric;
import taskContents.LocalMetric;
import taskContents.MetricName;

public class NMS_Agent {
    public class MetricResult{
        private LocalDateTime timeSent;
        private Message taskResult;

        public MetricResult(LocalDateTime timeSent, Message taskResult) {
            this.timeSent = timeSent;
            this.taskResult = taskResult;
        }

        public LocalDateTime getTimeSent() {
            return timeSent;
        }

        public Message getTaskResult() {
            return taskResult;
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj){
                return true;
            }

            if(obj == null || obj.getClass() != this.getClass()){
                return false;
            }

            MetricResult other = (MetricResult) obj;
            return this.timeSent.equals(other.timeSent) && this.taskResult.equals(other.taskResult);
        }
    }

    private final String SERVER_HOST_NAME = "127.0.0.1"; //TODO mudar conforme topologia
    private final int SERVER_UDP_PORT = 5000;
    private final int SERVER_TCP_PORT = 6000;
    private final int UDP_PORT = 7777;
    private final int MAX_RETRIES = 5;
    private final int TIMEOUT = 1000; // 1 segundo

    private DatagramSocket netTaskSocket;
    private InetAddress serverIP;
    private Map<MetricName, Integer> alertValues;
    private List<MetricResult> waitingAck;
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

        try{
            this.netTaskSocket = new DatagramSocket(UDP_PORT);
        }
        catch(SocketException e){
            System.err.println("ERROR: Could not open UDP socket: " + UDP_PORT);
        }
    }

    private void processConditions(Conditions conditions){
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
    }

    private void processTask(){
        int frequency = this.task.getFrequency();
        List<LocalMetric> localMetrics = this.task.getLocalMetrics();
        List<LinkMetric> linkMetrics = this.task.getLinkMetrics();

        for(LocalMetric localMetric: localMetrics){
            new MetricCollector(frequency, localMetric, null).run();
        }

        for(LinkMetric linkMetric: linkMetrics){
            new MetricCollector(frequency, null, linkMetric).run();
        }

        //thread a receber acks
        //while true
        for(MetricResult pair : this.waitingAck){
            //if(diferença timestam e data atual maior que time out)
            //  mandar mensagem de novo pela connection

        }

        // esperar por acks ?????????????????
        // lista mensagem que ainda nao tem ack na connection ou aqui?
        // ou map com localdatetime? para saber quando a mensagem foi enviada pela ultima vez e ver se já se passou o time out
        // se já passou o timeout enviar mensagem de novo
        // se não n fazer nada, esperar
        // quando se receber um ack, ir ao map remover a entrada
        // adicionar ao map quando se manda a mensagem
    }

    private void start() {
         try {
             Message msg = registerAgent();
             if(msg == null){
                 System.out.println("Erro ao registar agente no servidor");
                 return;
             }

             this.task = (Task) msg.getData();

             processConditions(this.task.getConditions());
             processTask();

             //while true
             // esperar algum tempo para fazer verificação?
             for(MetricResult pair : this.waitingAck){
                 //if(diferença timestam e data atual maior que time out)
                 //  mandar mensagem de novo pela connection
             }
         }
         finally{
             if(netTaskSocket != null && !netTaskSocket.isClosed()){
                 netTaskSocket.close();
             }
         }
     }

    private Message registerAgent() {
        Message msg = null;
        try {
            //Enviar pacote de registo ao servidor
            int seqNumber = new Random().nextInt(Integer.MAX_VALUE);
            byte[] byteMsg = (new Message(seqNumber, 0, MessageType.Regist, null)).getPDU();
            DatagramPacket sendPacket = new DatagramPacket(byteMsg, byteMsg.length, serverIP, SERVER_UDP_PORT);
            netTaskSocket.send(sendPacket);

            //Packet para receber resposta
            byte[] receiveMsg = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveMsg, receiveMsg.length);

            //Esperar TIMEOUT, se não receber a tarefa(confirmação), enviar novamente o registo
            boolean waiting = true;
            netTaskSocket.setSoTimeout(TIMEOUT);
            for (int i = 0; waiting && i < MAX_RETRIES; i++) {
                try {
                    netTaskSocket.receive(receivePacket);
                    waiting = false;
                } catch (SocketTimeoutException e) {
                    netTaskSocket.send(sendPacket);
                }
            }

            if(waiting) return null;

            msg = new Message(receivePacket.getData());

            //Enviar ACK ao servidor a confirmar a receção da tarefa(e confirmação do registo)
            byteMsg = (new Message(msg.getSeqNumber() + 1, msg.getSeqNumber(), MessageType.Ack, null)).getPDU();
            sendPacket = new DatagramPacket(byteMsg, byteMsg.length, serverIP, SERVER_UDP_PORT);
            netTaskSocket.send(sendPacket);
        }
        catch (Exception e){
            System.out.println("Erro ao enviar pacote de registo");
        }

        return msg;
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
