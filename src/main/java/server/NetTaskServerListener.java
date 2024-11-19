package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import message.Task;

public class NetTaskServerListener implements Runnable {
    private final String SERVER_HOST_NAME = "127.0.0.1"; //TODO mudar depois conforme a topologia
    private final int udpPort;
    private final Map<String, Task> tasks;
    private InetAddress serverIP;
    private DatagramSocket socket;

    public NetTaskServerListener(int port, Map<String, Task> tasks) {
        this.udpPort = port;
        try {
            this.serverIP = InetAddress.getByName(SERVER_HOST_NAME);
        } catch (UnknownHostException e) {
            System.err.println("ERROR: Could not resolve server hostname: " + SERVER_HOST_NAME);
        }

        this.tasks = tasks;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(udpPort, serverIP);
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                System.out.println("NetTaskListener iniciado na porta " + udpPort);
                socket.receive(packet);
                Thread dataHandler = new Thread(new NetTaskServerHandler(packet, tasks));
                dataHandler.start();
            }
        } catch (Exception e) {
            System.out.println("Erro ao receber pacote");
            e.printStackTrace();
        } finally {
            if(socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
