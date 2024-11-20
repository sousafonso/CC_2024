/**
 * @description: Cliente UDP no agente para enviar e receber tarefas e métricas via NetTask. Comunica-se com NetTaskHandler no servidor para registo e envio de métricas.
 * 
 * @responsibility:
 *  Enviar mensagens de registo e resultados de tarefas para o servidor via UDP. 
 */

package agent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;
import message.*;

//TODO rever se é necessário esta classe para uma thread ou se pode ficar no NMS_Agent
public class NetTaskClient implements Runnable {
    private final int UDP_PORT = 7777;
    private InetAddress serverIp;
    private int serverPort;
    private final AtomicInteger sequenceNumber = new AtomicInteger(0);
    private static final int MAX_RETRIES = 5;
    private static final int TIMEOUT = 1000; // 1 second

    public NetTaskClient(InetAddress serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(UDP_PORT);
            int seqNum = sequenceNumber.incrementAndGet();
            byte[] byteMsg = (new Message(seqNum, 0, MessageType.Regist, null)).getPDU();
            DatagramPacket registerPacket = new DatagramPacket(byteMsg, byteMsg.length, serverIp, serverPort);
            socket.send(registerPacket);

            byte[] receiveMsg = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveMsg, receiveMsg.length);
            socket.setSoTimeout(TIMEOUT);

            for (int i = 0; i < MAX_RETRIES; i++) {
                try {
                    socket.receive(receivePacket);
                    Message msg = new Message(receivePacket.getData());
                    if (msg.getType() == MessageType.Ack && msg.getSeqNumber() == seqNum) {
                        System.out.println("ACK received for sequence number: " + seqNum);
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("Timeout, retrying...");
                    socket.send(registerPacket);
                }
            }
        } catch (SocketException e) {
            System.out.println("UDP Socket Agent Error");
        } catch (IOException e) {
            System.out.println("Erro ao enviar pacote");
        } finally {
            if(socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

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

    /*private DatagramSocket socket;
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
    }*/
}
