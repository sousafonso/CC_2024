package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import message.Message;

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

    //private int port;
    //private DatagramSocket socket;
    private byte[] data = new byte[1024];

    public NetTaskHandler(byte[] data) {
        this.data = data;
    }

    private void processRegister(Message msg){
        //TODO Adicionar a clientes ativos ?
    }

    private void processTaskResult(Message msg){
        //TODO Guardar resultado da tarefa ?
    }

    private void processAck(Message msg){
        //TODO so receber ack / tirar de lista de pacotes ainda sem ack ?
    }

    @Override
    public void run() {
        Message msg = null;
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        
        try{
            ObjectInputStream is = new ObjectInputStream(in);
            msg = (Message) is.readObject();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Classe não encontrada");
            e.printStackTrace();
        }
        
        if(msg == null){
            System.out.println("Processamento da mensagem falhou");
            return;
        }

        switch (msg.getType()) {
            case Regist -> processRegister(msg);
            case TaskResult -> processTaskResult(msg);
            case Ack -> processAck(msg);
            default -> System.out.println("Tipo de mensagem não reconhecido");
        }
    }
}