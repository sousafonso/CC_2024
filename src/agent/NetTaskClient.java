package agent;

import java.net.*;
import java.net.InetAddress;

public class NetTaskClient {
    // private String serverAddress;
    // private int serverPort;

    // public NetTaskClient(String serverAddress, int serverPort) {
    //     this.serverAddress = serverAddress;
    //     this.serverPort = serverPort;
    // }

    // public void registerAgent() throws Exception {
    //     DatagramSocket socket = new DatagramSocket();
    //     InetAddress serverIP = InetAddress.getByName(serverAddress);
    //     String registrationMessage = "{\"agent_id\":\"agent_1\", \"request_task\": true}"; // tipo da mensagem em JSON (apenas um prototipo)
    //     byte[] buffer = registrationMessage.getBytes();
    //     DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverIP, serverPort);
    //     socket.send(packet);
    //     socket.close();
    // }

    // public void sendMetrics(String metrics) throws Exception {
    //     DatagramSocket socket = new DatagramSocket();
    //     InetAddress serverIP = InetAddress.getByName(serverAddress);
    //     byte[] buffer = metrics.getBytes();
    //     DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverIP, serverPort);
    //     socket.send(packet);
    //     socket.close();
    // }

    private DatagramSocket socket;

    public NetTaskClient() {
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    // Envia o resultado da tarefa para o servidor usando UDP
    public void sendTaskResult(TaskResult result) {
        try {
            String message = "TaskResult: " + result.getTaskId() + " - Success: " + result.isSuccess();
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName("localhost");

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, 5000); // Porta do servidor NetTask (UDP)
            socket.send(packet);
            System.out.println("Resultado da tarefa enviado via UDP: " + result.getTaskId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
