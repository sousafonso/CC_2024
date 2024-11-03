package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import message.Message;
import util.Util;

public class NetTaskHandler implements Runnable {
    private DatagramPacket packet;

    public NetTaskHandler(DatagramPacket packet) {
        this.packet = packet;
    }

    private void sendReply(Message msg){
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] buffer = Util.serialize(msg);
            
            DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, this.packet.getAddress(), this.packet.getPort());
            socket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("Erro ao enviar resposta");
            e.printStackTrace();
        } finally {
            if(socket != null && !socket.isClosed()) {
                socket.close();
            }
        }   
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
        Message msg = Util.deserialize(packet.getData());
        
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