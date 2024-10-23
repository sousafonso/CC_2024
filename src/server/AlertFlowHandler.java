package server;

import java.io.*;
import java.net.*;

public class AlertFlowHandler implements Runnable {
    private int tcpPort;

    public AlertFlowHandler(int port) {
        this.tcpPort = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(tcpPort);
            while (true) {
                // Aceitar conexões de agentes
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String alertMessage = in.readLine();
                System.out.println("Alerta recebido: " + alertMessage);

                // Confirmar o recebimento do alerta
                OutputStream out = clientSocket.getOutputStream();
                out.write("Alerta recebido com sucesso\n".getBytes());
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
