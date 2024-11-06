/**
 * @description: Cliente TCP no agente para enviar alertas críticos ao servidor. Comunica-se com AlertFlowHandler no servidor para enviar alertas críticos.
 * 
 * @responsibility:
 * Enviar alertas críticos para o servidor quando condições críticas forem detectadas.
 */

package agent;

import java.io.OutputStream;
import java.net.Socket;

public class AlertFlowClient {
    // private String serverAddress;
    // private int serverPort;

    // public AlertFlowClient(String serverAddress, int serverPort) {
    //     this.serverAddress = serverAddress;
    //     this.serverPort = serverPort;
    // }

    // // Método para enviar alertas críticos ao servidor via TCP
    // public void sendAlert(String alertMessage) {
    //     try (Socket socket = new Socket(serverAddress, serverPort)) {
    //         // Enviar o alerta para o servidor
    //         OutputStream out = socket.getOutputStream();
    //         out.write((alertMessage + "\n").getBytes());
    //         out.flush();

    //         System.out.println("Alerta enviado: " + alertMessage);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    /*public void sendAlert(Notification notification) {
        try (Socket socket = new Socket("localhost", 5001);
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {

            writer.println("Alerta: " + notification.getNotificationId() + " - " + notification.getMessage());
            System.out.println("Alerta enviado via TCP: " + notification.getNotificationId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
