package agent;

import message.Message;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Connection {
    private final String SERVER_HOST_NAME = "127.0.0.1"; //TODO mudar conforme topologia
    private final int SERVER_UDP_PORT = 5000;
    private final int SERVER_TCP_PORT = 6000;
    private final int UDP_PORT = 7777;
    private final int TIMEOUT;

    private Lock netTaskSendLock = new ReentrantLock();
    private Lock alertFlowSendLock = new ReentrantLock();

    private InetAddress serverIP;
    private DatagramSocket netTaskSocket;
    private Socket alertFlowSocket;
    private DataOutputStream alertFlowOut;
    public Connection(int timeout) {
        try {
            this.serverIP = InetAddress.getByName(SERVER_HOST_NAME); // guardar o IP do servidor para não ter que resolver o hostname a cada envio
        } catch (UnknownHostException e) {
            System.err.println("ERROR: Could not resolve server hostname: " + SERVER_HOST_NAME);
        }

        this.TIMEOUT = timeout;

        this.TIMEOUT = timeout;

        try {
            this.netTaskSocket = new DatagramSocket(UDP_PORT); // criar socket para enviar/receber pacotes via UDP
            this.netTaskSocket.setSoTimeout(TIMEOUT); // definir timeout para o socket
            this.alertFlowSocket = new Socket(SERVER_HOST_NAME, SERVER_TCP_PORT); // criar socket para enviar/receber pacotes via TCP
            this.alertFlowSocket.setSoTimeout(TIMEOUT); // definir timeout para o socket
            this.alertFlowOut = new DataOutputStream(new BufferedOutputStream(this.alertFlowSocket.getOutputStream())); // criar streams para enviar/receber dados
        } catch (SocketException e) {
            System.out.println("Erro ao criar sockets para conectar ao servidor");
        } catch (IOException e) {
            System.out.println("Erro ao criar AlertFlow Streams");
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

    public Message receiveViaUDP() throws SocketTimeoutException{
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);

        try {
            netTaskSocket.receive(packet);
        }
        catch (SocketTimeoutException e) {
            throw new SocketTimeoutException();
        }
        catch (IOException e) {
            System.out.println("Erro ao receber pacote via UDP do servidor.");
        }

        return new Message(packet.getData(), packet.getLength());
    }

    public void sendViaTCP(byte[] data){
        alertFlowSendLock.lock();
        try{
            alertFlowOut.write(data.length);
            alertFlowOut.write(data);
            alertFlowOut.flush();
        } catch (IOException e) {
            System.out.println("Erro ao enviar notificação de alerta ao servidor");
        }
        finally {
            alertFlowSendLock.unlock();
        }
    }

    public void close(){
        if (netTaskSocket != null && !netTaskSocket.isClosed()) {
            netTaskSocket.close();
        }
    }
}