/**
 * @description: Cliente UDP no agente para enviar e receber tarefas e métricas via NetTask. Comunica-se com NetTaskHandler no servidor para registo e envio de métricas.
 * 
 * @responsibility:
 *  Enviar mensagenspackage agent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;
import message.*;

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
} de registo e resultados de tarefas para o servidor via UDP. 
 */

package agent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;
import message.*;

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
}
