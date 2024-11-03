package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class NetTaskListener implements Runnable {
    private final int udpPort;
    private DatagramSocket socket;

    public NetTaskListener(int port) {
        this.udpPort = port;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(udpPort);
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                System.out.println("NetTaskListener iniciado na porta " + udpPort);
                socket.receive(packet);
                Thread dataHandler = new Thread(new NetTaskHandler(packet));
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
