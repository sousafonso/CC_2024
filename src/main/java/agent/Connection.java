package agent;

import message.Message;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* Conexão centralizada para o NMS_Agent
   Contém as informações necessárias para a comunicação com o servidor e gere a concorrência na utilização dos sockets entre as threads
*/
public class Connection {
    private final String SERVER_HOST_NAME = "10.0.4.10"; //TODO mudar conforme topologia
    private final int SERVER_UDP_PORT = 5000;
    private final int SERVER_TCP_PORT = 6000;
    private final int UDP_PORT = 7777;
    private final int TIMEOUT;
    private Lock netTaskSendLock = new ReentrantLock();
    private InetAddress serverIP;
    private DatagramSocket netTaskSocket;

    public Connection(int timeout, String agentHostName) {
        this.TIMEOUT = timeout;

        try {
            this.serverIP = InetAddress.getByName(SERVER_HOST_NAME);
            this.netTaskSocket = new DatagramSocket(UDP_PORT, InetAddress.getByName(agentHostName));
            this.netTaskSocket.setSoTimeout(TIMEOUT);
        } catch (UnknownHostException e) {
            System.err.println("ERROR: Could not resolve hostname");
        } catch (SocketException e) {
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
        try{
            Socket alertFlowSocket = new Socket(SERVER_HOST_NAME, SERVER_TCP_PORT);
            alertFlowSocket.setSoTimeout(TIMEOUT);
            DataOutputStream alertFlowOut = new DataOutputStream(new BufferedOutputStream(alertFlowSocket.getOutputStream()));

            alertFlowOut.writeInt(data.length);
            alertFlowOut.write(data);
            alertFlowOut.flush();

            alertFlowOut.close();
            alertFlowSocket.close();
        } catch (IOException e) {
            System.out.println("Erro ao enviar notificação de alerta ao servidor");
        }
    }

    public void close(){
        if (netTaskSocket != null && !netTaskSocket.isClosed()) {
            netTaskSocket.close();
        }
    }
}
