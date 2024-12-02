/**
 * NMS_Server
 * @description: Classe principal do servidor, responsável por inicializar os componentes e gerenciar a comunicação com os agentes.
 *  Interage com o NetTaskHandler e o AlertFlowHandler para receber e processar mensagens dos agentes.
    Utiliza StorageModule para armazenar métricas e alertas.
 * 
 * @responsibility:
 *  Carregar configurações e tarefas iniciais de config.json usando JSONTaskReader.
    Iniciar os handlers NetTaskHandler (UDP) e AlertFlowHandler (TCP) para receber dados dos agentes.
    Armazenar métricas e alertas usando o StorageModule.
    Distribuir tarefas para os agentes.
 */

package server;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import message.*;
import storage.StorageModule;

public class NMS_Server {
    private final int TCP_PORT = 6000;

    private StorageModule storageModule;
    private JSONTaskReader taskReader;

    public NMS_Server() {
        this.storageModule = new StorageModule(); // Inicializa o módulo de armazenamento
        this.taskReader = new JSONTaskReader(); // Inicializa o leitor de tarefas
    }

    public void initialize() {
        System.out.println("A iniciar servidor");

        // Carregar tarefas do JSON de configuração
        Map<String, Task> tasks = this.taskReader.readJson();

        // não deveria ser passado uma UDP_PORT? para o NetTaskServerHandler?
        Thread NetTaskListener = new Thread(new NetTaskServerListener(tasks, storageModule));
        Thread AlertFlowListener = new Thread(new AlertFlowListener(TCP_PORT, storageModule));
        NetTaskListener.start();
        AlertFlowListener.start();

        //TODO Tratar isto das métricas
        // Iniciar exibição periódica de métricas a cada 10 segundos
        startMetricDisplayScheduler();
        startInteractiveMenu();
    }

    private void startInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Consultar métricas e alertas");
            System.out.println("2. Sair");
            System.out.print("Escolha uma opção: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consumir nova linha

            switch (option) {
                case 1:
                    System.out.print("Digite o ID do cliente: ");
                    String clientId = scanner.nextLine();
                    storageModule.displayMetricsAndAlerts(clientId);
                    break;
                case 2:
                    System.out.println("A sair...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void startMetricDisplayScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> storageModule.displayMetricsAndAlerts("default"), 0, 10, TimeUnit.SECONDS); // ver "default"
    }

    public static void main(String[] args) {
        NMS_Server server = new NMS_Server();
        server.initialize();
    }
}