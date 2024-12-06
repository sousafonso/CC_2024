package server;

import message.Message;
import message.MessageType;
import message.Notification;
import storage.StorageModule;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class AlertFlowListener implements Runnable {
    private final int TCP_PORT = 6000;
    private ServerSocket serverSocket;
    private StorageModule storageModule;

    public AlertFlowListener(StorageModule storageModule) {
        try {
            this.serverSocket = new ServerSocket(TCP_PORT);
        } catch (IOException e) {
            System.err.println("Erro na criação do socket para AlertFlow");
        }
        this.storageModule = storageModule;
    }

    private void handleClient(Socket clientSocket) {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()))) {
            int length = in.readInt();
            byte[] data = new byte[length];
            in.readFully(data);

            Message alert = new Message(data, length);
            if(alert.getType() == MessageType.Notification) {
                this.storageModule.storeAlert(clientSocket.getInetAddress().getHostAddress(), (Notification) alert.getData());
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler mensagens do cliente por AlertFlow");
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            } catch (IOException e) {
                System.err.println("Erro ao aceitar conexão AlertFlow de um cliente");
            }
            if(Thread.interrupted()) {
                break;
            }
        }
    }
}
