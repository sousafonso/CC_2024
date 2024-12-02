package server;

import storage.StorageModule;
import message.Notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class AlertFlowListener implements Runnable {
    private int tcpPort;
    private ServerSocket serverSocket;
    private StorageModule storageModule;

    public AlertFlowListener(int port, StorageModule storageModule) {
        this.tcpPort = port;
        this.storageModule = storageModule;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(tcpPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String receivedData = reader.readLine();
                Notification notification = new Notification(receivedData.split(";"));
                storageModule.storeAlert(notification.getTaskID(), receivedData, notification);
                System.out.println("Alerta recebido: " + notification.getMeasurement());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}