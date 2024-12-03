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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import message.Message;
import message.MessageType;
import message.Notification;
import storage.StorageModule;

public class AlertFlowHandler implements Runnable {
    private int port;
    private StorageModule storageModule;

    public AlertFlowHandler(int port, StorageModule storageModule) {
        this.port = port;
        this.storageModule = storageModule;
    }

    @Override
    public void run() {
    }
}