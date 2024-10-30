package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AlertFlowHandler implements Runnable {
    // private int port;
    // private ServerSocket serverSocket;

    // public AlertFlowHandler(int port) {
    //     this.port = port;
    //     try {
    //         this.serverSocket = new ServerSocket(port);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    // @Override
    // public void run() {
    //     while (true) {
    //         try {
    //             Socket clientSocket = serverSocket.accept();
    //             System.out.println("Conexão de alerta recebida");
    //             // Processamento adicional aqui
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }

    private int port;
    private ServerSocket serverSocket;

    public AlertFlowHandler(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("AlertFlowHandler à espera de conexões TCP na porta " + port);

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexão de alerta recebida via TCP");

                // Ler a mensagem de alerta
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String alertMessage = reader.readLine();
                System.out.println("Alerta recebido: " + alertMessage);

                // Processar o alerta aqui (por exemplo, gerar logs ou notificações internas)

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}