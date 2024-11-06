/**
 * AlertFlowHandler.java
 * @description: Handler que gere a comunicação de alertas via TCP. Recebe alertas de AlertFlowClient (do NMS_Agent).
Interage com StorageModule para armazenar alertas.
 * 
 * @responsibility:
 *  Receber alertas críticos dos agentes via TCP.
    Armazenar e exibir alertas quando condições críticas são detectadas.    
 */

package server;

public class AlertFlowHandler implements Runnable {

    @Override
    public void run() {
    }
}