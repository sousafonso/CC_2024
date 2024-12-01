package agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Connection {
    private final String SERVER_HOST_NAME = "127.0.0.1"; //TODO mudar conforme topologia
    private final int SERVER_UDP_PORT = 5000;
    private final int SERVER_TCP_PORT = 6000;
    private final int UDP_PORT = 7777;
    private final int TIMEOUT = 1000; // 1 segundo

    private Lock netTaskSendLock = new ReentrantLock();
    private Lock netTaskReceiveLock = new ReentrantLock();

    private InetAddress serverIP;
    private DatagramSocket netTaskSocket;
    private Socket tcpSocket;
    private DataOutputStream tcpOut;
    private DataInputStream tcpIn;

    public Connection() {
        try {
            this.serverIP = InetAddress.getByName(SERVER_HOST_NAME);
        } catch (UnknownHostException e) {
            System.err.println("ERROR: Could not resolve server hostname: " + SERVER_HOST_NAME);
        }

        try {
            this.netTaskSocket = new DatagramSocket(UDP_PORT);
            this.netTaskSocket.setSoTimeout(TIMEOUT);
            this.tcpSocket = new Socket();
        }
        catch (SocketException e) {
            System.out.println("Erro ao criar sockets para conectar ao servidor");
        }
    }

    public void sendViaUDP(byte[] data){
        DatagramPacket packet = new DatagramPacket(data, data.length, this.serverIP, SERVER_UDP_PORT);

        netTaskSendLock.lock();
        try {
            netTaskSocket.send(packet);
        } catch (IOException e) {
            System.out.println("Erro ao enviar pacote via UDP para o servidor.");
        } finally {
            netTaskSendLock.unlock();
        }
    }

    public byte[] receiveViaUDP() throws SocketTimeoutException{
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);

        netTaskReceiveLock.lock();
        try {
            netTaskSocket.receive(packet);
        }
        catch (SocketTimeoutException e) {
            throw new SocketTimeoutException();
        }
        catch (IOException e) {
            System.out.println("Erro ao receber pacote via UDP do servidor.");
        }
        finally {
             netTaskReceiveLock.unlock();
        }

        return packet.getData();
    }

    public void sendViaTCP(byte[] data){
        netTaskSendLock.lock();
        try {
            if (tcpSocket.isClosed()) {
                tcpSocket = new Socket(serverIP, SERVER_TCP_PORT);
                tcpOut = new DataOutputStream(tcpSocket.getOutputStream());
            }
            tcpOut.writeInt(data.length); // Envia o tamanho dos dados primeiro
            tcpOut.write(data); // Envia os dados
            tcpOut.flush();
        } catch (IOException e) {
            System.out.println("Erro ao enviar dados via TCP para o servidor.");
            e.printStackTrace();
        } finally {
            netTaskSendLock.unlock();
        }
    }

    public byte[] receiveViaTCP(){
        netTaskReceiveLock.lock();
        try {
            if (tcpSocket.isClosed()) {
                tcpSocket = new Socket(serverIP, SERVER_TCP_PORT);
                tcpIn = new DataInputStream(tcpSocket.getInputStream());
            }
            int length = tcpIn.readInt(); // Lê o tamanho dos dados
            byte[] data = new byte[length];
            tcpIn.readFully(data); // Lê os dados
            return data;
        } catch (IOException e) {
            System.out.println("Erro ao receber dados via TCP do servidor.");
            e.printStackTrace();
            return null;
        } finally {
            netTaskReceiveLock.unlock();
        }
    }

    public void close() {
        if (netTaskSocket != null && !netTaskSocket.isClosed()) {
            netTaskSocket.close();
        }
        if (tcpSocket != null && !tcpSocket.isClosed()) {
            try {
                tcpSocket.close();
            } catch (IOException e) {
                System.out.println("Erro ao fechar o socket TCP.");
                e.printStackTrace();
            }
        }
    }
}
