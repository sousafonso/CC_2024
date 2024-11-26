package agent;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Connection {
    private final String SERVER_HOST_NAME = "127.0.0.1"; //TODO mudar conforme topologia
    private final int SERVER_UDP_PORT = 5000;
    private final int SERVER_TCP_PORT = 6000;
    private final int UDP_PORT = 7777;

    private Lock udpSendLock = new ReentrantLock();
    private Lock udpReceiveLock = new ReentrantLock();

    private InetAddress serverIP;
    private DatagramSocket udpSocket;
    private Socket tcpSocket;

    public Connection() {
        try {
            this.serverIP = InetAddress.getByName(SERVER_HOST_NAME);
        } catch (UnknownHostException e) {
            System.err.println("ERROR: Could not resolve server hostname: " + SERVER_HOST_NAME);
        }

        try {
            this.udpSocket = new DatagramSocket(UDP_PORT);
            this.tcpSocket = new Socket();
        }
        catch (SocketException e) {
            System.out.println("Erro ao criar sockets para conectar ao servidor");
        }
    }

    public void sendViaUDP(byte[] data){
        DatagramPacket packet = new DatagramPacket(data, data.length, this.serverIP, SERVER_UDP_PORT);

        udpSendLock.lock();
        try {
            udpSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Erro ao enviar pacote via UDP para o servidor.");
        } finally {
            udpSendLock.unlock();
        }
    }

    public byte[] receiveViaUDP(){
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);

        udpReceiveLock.lock();
        try {
            udpSocket.receive(packet);
        } catch (IOException e) {
            System.out.println("Erro ao receber pacote via UDP do servidor.");
        } finally {
            udpReceiveLock.unlock();
        }

        return packet.getData();
    }

    public void sendViaTCP(byte[] data){}

    public void receiveViaTCP(byte[] data){}
}
