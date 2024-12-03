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
            this.socket = new DatagramSocket(UDP_PORT);
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                this.socket.receive(packet);
                Thread handler = new Thread(new NetTaskServerHandler(packet, tasks, storage));
                handler.start();
            }
        } catch (Exception e) {
            System.err.println("ERROR: Could not start server listener");
            e.printStackTrace();
        } finally {
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
        }
    }
}
