/**
 * @description: Cliente TCP no agente para enviar alertas críticos ao servidor. Comunica-se com AlertFlowHandler no servidor para enviar alertas críticos.
 * 
 * @responsibility:
 * Enviar alertas críticos para o servidor quando condições críticas forem detectadas.
 */

package agent;

import java.io.OutputStream;
import java.net.Socket;

import message.Notification;

public class AlertFlowClient {
    private String serverAddress;
    private int serverPort;

    public AlertFlowClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void sendAlert(Notification notification) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             OutputStream output = socket.getOutputStream()) {
            output.write(notification.getPayload().getBytes());
            output.flush();
            System.out.println("Alerta enviado: " + notification.getNotificationId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
