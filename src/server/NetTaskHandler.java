package server;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class NetTaskHandler implements Runnable {
    private int udpPort;

    public NetTaskHandler(int port) {
        this.udpPort = port;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(udpPort);
            byte[] buffer = new byte[1024];

            while (true) {
                // Receber dados (registro ou métricas dos agentes)
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String receivedData = new String(packet.getData(), 0, packet.getLength());

                // Processar a mensagem (registrar agente, enviar tarefas ou armazenar métricas)
                System.out.println("Recebido: " + receivedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}