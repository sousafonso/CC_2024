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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import message.Message;
import message.MessageType;
import message.Task;
import message.TaskResult;
import storage.StorageModule;

public class NetTaskServerHandler implements Runnable {
    private final Map<String, Task> tasks;
    private DatagramPacket packet;
    private StorageModule storageModule;

    public NetTaskServerHandler(DatagramPacket packet, Map<String, Task> tasks, StorageModule storageModule) {
        this.packet = packet;
        this.tasks = tasks;
        this.storageModule = storageModule;
    }

    // Envia resposta relativamente a uma mensagem recebida (ack, erro, etc) 
    private void sendReply(Message msg){
        DatagramSocket socket = null;
            try {
                 socket = new DatagramSocket();
                 byte[] buffer = msg.getPDU();
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
        System.out.println("[RECEBIDO] registo de " + packet.getAddress());
        Message reply;
        String sourceAddress = packet.getAddress().getHostAddress();
        Task agentTask = tasks.get(sourceAddress);
        int newSeqNumber = msg.getSeqNumber() + 1;

        if(agentTask == null){
            System.out.println("[ENVIAR] registo com ACK para " + packet.getAddress());
            reply = new Message(newSeqNumber, msg.getSeqNumber(), MessageType.Ack, null);
        }
        else{
            System.out.println("[ENVIAR] a registo com TASK para " + packet.getAddress());
            reply = new Message(newSeqNumber, msg.getSeqNumber(), MessageType.Task, agentTask);
            NetTaskServerListener.addToAckWaitingList(LocalDateTime.now(), reply, packet.getAddress(), packet.getPort());
        }

        sendReply(reply);
    }

    private void processTaskResult(Message msg) {
        TaskResult taskResult = (TaskResult) msg.getData();
        System.out.println("[RECEBIDO] Task Result de " + packet.getAddress()+ " : " + taskResult.getTaskId() + " -> " + taskResult.getMetricName() + " " + taskResult.getResult());
        String deviceId = packet.getAddress().getHostAddress();
        storageModule.storeMetric(deviceId, taskResult.getMetricName(), taskResult.getResult());
        System.out.println("[Enviar] ACK para " + packet.getAddress() + " relativo a TaskResult" + taskResult.getTaskId() + " -> " + taskResult.getMetricName() + " " + taskResult.getResult());
        sendReply(new Message(msg.getSeqNumber() + 1, msg.getSeqNumber(), MessageType.Ack, null));
    }

    private void processAck(Message msg){
        System.out.println("[RECEBIDO] ACK de " + packet.getAddress());
        NetTaskServerListener.removeFromAckWaitingList(msg.getAckNumber());
    }

    @Override
    public void run() {
        Message msg = new Message(packet.getData(), packet.getLength());
        if (msg == null) {
            System.out.println("Processamento da mensagem falhou");
            return;
        }

        switch (msg.getType()) {
            case Regist:
                processRegister(msg);
                break;
            case TaskResult:
                processTaskResult(msg);
                break;
            case Ack:
                processAck(msg);
                break;
            default:
                System.out.println("Tipo de mensagem desconhecido");
                break;
        }
    }
}