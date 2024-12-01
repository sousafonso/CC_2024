package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import message.Task;
import storage.StorageModule;

public class NetTaskServerListener implements Runnable {
    private final String SERVER_HOST_NAME = "127.0.0.1"; //TODO mudar depois conforme a topologia
    private final int UDP_PORT = 5000;
    private final Map<String, Task> tasks;
    private StorageModule storage;
    private InetAddress serverIP;
    private DatagramSocket socket;
    private static final int MAX_RETRIES = 5;
    private static final int TIMEOUT = 1000; // 1 second

    public NetTaskServerListener(Map<String, Task> tasks, StorageModule storage) {
        try {
            this.serverIP = InetAddress.getByName(SERVER_HOST_NAME);
        } catch (UnknownHostException e) {
            System.err.println("ERROR: Could not resolve server hostname: " + SERVER_HOST_NAME);
        }
        this.tasks = tasks;
        this.storage = storage;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(UDP_PORT, serverIP);
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                new Thread(new NetTaskServerHandler(packet, tasks, storage)).start();
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
