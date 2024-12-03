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
    }
}