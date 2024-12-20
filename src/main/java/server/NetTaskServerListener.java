package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import message.Message;
import message.Task;
import storage.StorageModule;

public class NetTaskServerListener implements Runnable {
    private final int UDP_PORT = 5000;
    private final Duration TIMEOUT = Duration.ofMillis(3000);
    private final int MAX_RETRIES = 5;
    private final Map<String, Task> tasks;
    private static ConcurrentHashMap<Integer, AckStatus> waitingAck = new ConcurrentHashMap<>();
    private static Lock lock = new ReentrantLock();
    private static Set<Integer> receivedPackets = new HashSet<>();
    private StorageModule storage;
    private DatagramSocket socket;

    public NetTaskServerListener(Map<String, Task> tasks, StorageModule storage) {
        try {
            this.socket = new DatagramSocket(UDP_PORT);
        } catch (IOException e) {
            System.err.println("Erro ao criar NetTask socket no servidor");
        }
        this.tasks = tasks;
        this.storage = storage;
    }

    public static void addToAckWaitingList(LocalDateTime timeSent, Message taskResult, InetAddress address, int port) {
        waitingAck.put(taskResult.getSeqNumber(), new AckStatus(timeSent, taskResult, address, port));
    }

    public static void removeFromAckWaitingList(int ackNumber) {
        waitingAck.remove(ackNumber);
    }

    public static void addToReceivedPackets(int seqNumber) {
        lock.lock();
        try{
            receivedPackets.add(seqNumber);
        } finally {
            lock.unlock();
        }
    }

    public static boolean alreadyReceived(int seqNumber) {
        lock.lock();
        try {
            return receivedPackets.contains(seqNumber);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        try {
            Thread packetRetransmiter = new Thread(() -> {
                while (true) {
                    for(AckStatus status : waitingAck.values()) {
                        if (status.getTries() > MAX_RETRIES) {
                            waitingAck.remove(status.getMessage().getSeqNumber());
                            continue;
                        }

                        Duration timeDifference = Duration.between(status.getTimeSent(), LocalDateTime.now());
                        if (timeDifference.compareTo(TIMEOUT) > 0) {
                            try {
                                byte[] msgBytes = status.getMessage().getPDU();
                                DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, status.getSourceAddress(), status.getSourcePort());
                                System.out.println("[RE-ENVIO] de mensagem para " + status.getSourceAddress() + ":" + status.getSourcePort());
                                this.socket.send(packet);
                                status.setTimeSent(LocalDateTime.now());
                                status.incTries();
                                waitingAck.put(status.getMessage().getSeqNumber(), status);
                            } catch (IOException e) {
                                System.err.println("Erro ao re-enviar pacote para o cliente");
                            }
                        }
                    }
                    try {
                        Thread.sleep(TIMEOUT);
                    } catch (InterruptedException ignored) {
                        break;
                    }
            }});

            packetRetransmiter.start();

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                this.socket.receive(packet);
                Thread handler = new Thread(new NetTaskServerHandler(packet, tasks, storage));
                handler.start();
                if(Thread.interrupted()) {
                    packetRetransmiter.interrupt();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: Could not start server listener");
        } finally {
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
        }
    }
}
