package server;

import java.net.*;

public class NetTaskListener implements Runnable {
    private int port;
    private ServerSocket serverSocket;

    public NetTaskListener(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new NetTaskHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private int udpPort;

    // public NetTaskListener(int port) {
    //     this.udpPort = port;
    // }

    // @Override
    // public void run() {
    //     try {
    //         DatagramSocket socket = new DatagramSocket(udpPort);
    //         byte[] buffer = new byte[1024];
            
    //         System.out.println("NetTaskListener iniciado na porta " + udpPort);
    //         while (true) {
    //             DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    //             socket.receive(packet);
    //             String data = new String(packet.getData()); //TODO mudar depois para dados concretos

    //             System.out.println("Server recebeu: " + data); //TODO processar dados com NetTaskHandler
    //         }
            
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
}
