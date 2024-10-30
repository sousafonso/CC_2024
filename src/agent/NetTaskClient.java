package agent;

import java.net.*;
import java.net.InetAddress;

public class NetTaskClient {
    // private String serverAddress;
    // private int serverPort;

    // public NetTaskClient(String serverAddress, int serverPort) {
    //     this.serverAddress = serverAddress;
    //     this.serverPort = serverPort;
    // }

    // public void registerAgent() throws Exception {
    //     DatagramSocket socket = new DatagramSocket();
    //     InetAddress serverIP = InetAddress.getByName(serverAddress);
    //     String registrationMessage = "{\"agent_id\":\"agent_1\", \"request_task\": true}"; // tipo da mensagem em JSON (apenas um prototipo)
    //     byte[] buffer = registrationMessage.getBytes();
    //     DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverIP, serverPort);
    //     socket.send(packet);
    //     socket.close();
    // }

    // public void sendMetrics(String metrics) throws Exception {
    //     DatagramSocket socket = new DatagramSocket();
    //     InetAddress serverIP = InetAddress.getByName(serverAddress);
    //     byte[] buffer = metrics.getBytes();
    //     DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverIP, serverPort);
    //     socket.send(packet);
    //     socket.close();
    // }

    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private AtomicInteger sequenceNumber = new AtomicInteger(0);

    public NetTaskClient(String serverIp, int serverPort) {
        try {
            this.socket = new DatagramSocket();
            this.serverAddress = InetAddress.getByName(serverIp);
            this.serverPort = serverPort;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Envio de métricas com controle de sequência
    public void sendTaskResult(TaskResult result) {
        try {
            int seqNum = sequenceNumber.incrementAndGet();
            String message = "SEQ:" + seqNum + " TaskResult: " + result.getTaskId() + " - Success: " + result.isSuccess();
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);

            // Envia a mensagem e espera por ACK
            socket.send(packet);
            socket.setSoTimeout(1000);  // Timeout de 1 segundo para retransmissão

            // Escuta por ACK
            byte[] ackBuffer = new byte[256];
            DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
            socket.receive(ackPacket);

            String ackMessage = new String(ackPacket.getData(), 0, ackPacket.getLength());
            if (ackMessage.equals("ACK:" + seqNum)) {
                System.out.println("ACK recebido para SEQ:" + seqNum);
            } else {
                System.out.println("ACK incorreto. Retransmitindo...");
                sendTaskResult(result); // Retransmissão
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
