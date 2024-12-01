package server;

import storage.StorageModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class AlertFlowListener implements Runnable{
    private int tcpPort;
    private ServerSocket serverSocket;

    public AlertFlowListener(int port, StorageModule storageModule) {
        this.tcpPort = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(tcpPort);
            System.out.println("AlertFlowListener iniciado na porta " + tcpPort);
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String receivedData = reader.readLine();
                System.out.println("Notificação recebida: " + receivedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(serverSocket != null && !serverSocket.isClosed()) {
                try{
                serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}