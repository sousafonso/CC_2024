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
import taskContents.MetricName;

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

        startDisplayScheduler();
        runMenu();
    }

    private void runMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu de Consultas:");
            System.out.println("1. Consultar Métricas de um Dispositivo");
            System.out.println("2. Consultar Alertas de um Dispositivo");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumir a linha restante

            switch (choice) {
                case 1 -> consultMetrics(scanner);
                case 2 -> consultAlerts(scanner);
                case 3 -> {
                    System.out.println("Encerrando o servidor...");
                    return;
                }
                default -> System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    private void consultMetrics(Scanner scanner) {
        System.out.print("Introduza o ID do dispositivo: ");
        String deviceId = scanner.nextLine();

        var metrics = storageModule.getMetrics(deviceId);
        if (metrics.isEmpty()) {
            System.out.println("Sem métricas registadas para este dispositivo.");
            return;
        }

        System.out.println("\nMétricas registadas:");
        for (var entry : metrics.entrySet()) {
            MetricName metricName = entry.getKey();
            StorageModule.MetricStats stats = entry.getValue();
            System.out.printf("%s - Último Valor: %.2f, Mínimo: %.2f, Máximo: %.2f%n",
                    metricName, stats.getLastValue(), stats.getMinValue(), stats.getMaxValue());
        }
    }

    private void consultAlerts(Scanner scanner) {
        System.out.print("Introduza o ID do dispositivo: ");
        String deviceId = scanner.nextLine();

        var alerts = storageModule.getAlerts(deviceId);
        if (alerts.isEmpty()) {
            System.out.println("Sem alertas registados para este dispositivo.");
            return;
        }

        System.out.println("\nAlertas registados:");
        for (Notification alert : alerts) {
            System.out.printf("Métrica: %s, Valor: %.2f, Timestamp: %s%n",
                    alert.getMetricName(), alert.getMeasurement(), alert.getTimestamp());
        }
    }

    private void startDisplayScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            storageModule.displayAllMetrics();
            storageModule.displayAllAlerts();
        }, 0, 10, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        NMS_Server server = new NMS_Server();
        server.initialize();
    }
}