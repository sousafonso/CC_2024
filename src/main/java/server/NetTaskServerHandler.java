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
    private Lock lock = new ReentrantLock();
    private static Map<String, List<Integer>> ackWaitingList = new HashMap<>();
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
        Message reply;
        String sourceAddress = packet.getAddress().getHostAddress();
        Task agentTask = tasks.get(sourceAddress);
        int newSeqNumber = msg.getSeqNumber() + 1;

        if(agentTask == null){
            reply = new Message(newSeqNumber, msg.getSeqNumber(), MessageType.Ack, null);
        }
        else{
            reply = new Message(newSeqNumber, msg.getSeqNumber(), MessageType.Task, agentTask);
        }

        lock.lock();
        try {
            ackWaitingList.computeIfAbsent(sourceAddress, k -> new ArrayList<>()).add(newSeqNumber);
        }
        finally {
            lock.unlock();
        }

        sendReply(reply);
    }

    private void processTaskResult(Message msg) {
        System.out.println("Task Result Received: " + msg.toString());
        TaskResult taskResult = (TaskResult) msg.getData();
        String taskId = taskResult.getTaskId();
        storageModule.storeTaskResult(taskId, taskId, taskResult);
    }

    private void processAck(Message msg){
        String sourceAddress = packet.getAddress().getHostAddress();
        int ackNumber = msg.getAckNumber();

        lock.lock();
        try {
            if (ackWaitingList.containsKey(sourceAddress)) {
                List<Integer> list = ackWaitingList.get(sourceAddress);
                list.remove((Integer) ackNumber);
                ackWaitingList.put(sourceAddress, list);
            }
        }
        finally {
            lock.unlock();
        }
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