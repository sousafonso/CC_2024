package server;

import java.net.*;
import java.util.List;

public class NetTaskHandler implements Runnable {
    // private int port;
    // private DatagramSocket socket;

    // public NetTaskHandler(int port) {
    //     this.port = port;
    //     try {
    //         this.socket = new DatagramSocket(port);
    //     } catch (SocketException e) {
    //         e.printStackTrace();
    //     }
    // }

    // @Override
    // public void run() {
    //     // Lógica para escutar mensagens dos agentes (se necessário)
    // }

    // public void sendTaskToAgents(Task task) {
    //     try {
    //         String message = "Task ID: " + task.getTaskId() + ", Frequency: " + task.getFrequency();
    //         byte[] buffer = message.getBytes();
    //         InetAddress agentAddress = InetAddress.getByName("localhost");

    //         DatagramPacket packet = new DatagramPacket(buffer, buffer.length, agentAddress, 5001);
    //         socket.send(packet);
    //         System.out.println("Tarefa enviada para o agente: " + task.getTaskId());
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    private int port;
    private DatagramSocket socket;

    public NetTaskHandler(int port) {
        this.port = port;
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        System.out.println("NetTaskHandler à espera de pacotes UDP na porta " + port);

        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Mensagem recebida via UDP: " + receivedMessage);

                // Processar o resultado da tarefa aqui (por exemplo, armazenar ou gerar logs)
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Método para enviar tarefas aos agentes (se necessário)
    public void sendTaskToAgents(Task task) {
        try {
            String message = "Task ID: " + task.getTaskId() + ", Frequency: " + task.getFrequency();
            byte[] buffer = message.getBytes();
            InetAddress agentAddress = InetAddress.getByName("localhost");

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, agentAddress, 5001); // Porta do agente
            socket.send(packet);
            System.out.println("Tarefa enviada para o agente via UDP: " + task.getTaskId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}