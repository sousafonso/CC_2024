package server;

import java.io.*;
import java.net.*;

public class AlertFlowListener implements Runnable{
    private int tcpPort;

    public AlertFlowListener(int port) {
        this.tcpPort = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(tcpPort);
            System.out.println("AlertFlowListener iniciado na porta " + tcpPort);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("AlertFlow conectado: " + socket.getInetAddress().getHostAddress());

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String receivedData = reader.readLine();

                System.out.println("Recebido: " + receivedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}