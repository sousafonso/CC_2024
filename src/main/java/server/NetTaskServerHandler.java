/**
 * @description: Handler que gere a comunicação de tarefas e recolha de métricas via UDP.
 * Recebe dados de NetTaskClient (do NMS_Agent). Armazena métricas no StorageModule e comunica-se com o NMS_Server.
 * 
 * @responsibility: 
 * - Receber registos de agentes e resultados de tarefas via UDP.
   - Enviar tarefas para os agentes registados.

 */

package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import message.Message;
import message.MessageType;
import util.Util;

public class NetTaskServerHandler implements Runnable {
    private DatagramPacket packet;

    public NetTaskServerHandler(DatagramPacket packet) {
        this.packet = packet;
    }

    // Envia resposta relativamente a uma mensagem recebida (ack, erro, etc) 
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

    // Processa a mensagem recebida e executa a ação correspondente
    private void processRegister(Message msg){
        System.out.println(msg.toString());
        Message reply = new Message(msg.getSeqNumber() + 1, msg.getSeqNumber(), "tarefa:5 flexoes", MessageType.Task);
        sendReply(reply);
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