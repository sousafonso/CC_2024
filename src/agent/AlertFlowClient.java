package agent;

import java.io.OutputStream;
import java.net.Socket;

public class AlertFlowClient {
    private String serverAddress;
    private int serverPort;

    public AlertFlowClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    // Método para enviar alertas críticos ao servidor via TCP
    public void sendAlert(String alertMessage) {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            // Enviar o alerta para o servidor
            OutputStream out = socket.getOutputStream();
            out.write((alertMessage + "\n").getBytes());
            out.flush();

            System.out.println("Alerta enviado: " + alertMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
