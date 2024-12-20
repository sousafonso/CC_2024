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

import java.util.*;

import message.*;
import storage.MetricStats;
import taskContents.LinkMetric;
import taskContents.LocalMetric;
import taskContents.MetricName;
import storage.StorageModule;

public class NMS_Server {
    private StorageModule storageModule;
    private JSONTaskReader taskReader;
    private Map<String, Task> tasks;
    private Map<String, List<MetricName>> agentMetrics;
    private String activeAgent = "";

    public NMS_Server() {
        this.storageModule = new StorageModule(); // Inicializa o módulo de armazenamento
        this.taskReader = new JSONTaskReader(); // Inicializa o leitor de tarefas
    }

    public void initialize() {
        System.out.println("A iniciar servidor");

        // Carregar tarefas do JSON de configuração
        this.tasks = this.taskReader.readJson();
        getMetricsByAgent();

        Thread NetTaskListener = new Thread(new NetTaskServerListener(tasks, storageModule));
        Thread AlertFlowListener = new Thread(new AlertFlowListener(storageModule));
        AlertFlowListener.start();
        NetTaskListener.start();

        runMenu();

        AlertFlowListener.interrupt();
        NetTaskListener.interrupt();
        System.exit(0);
    }

    private void getMetricsByAgent() {
        this.agentMetrics = new HashMap<>();
        for (Map.Entry<String, Task> entry : tasks.entrySet()) {
            Task task = entry.getValue();
            List<MetricName> metricNames = new ArrayList<>();

            for(LocalMetric metric : task.getLocalMetrics()) {
                metricNames.add(metric.getMetricName());
            }

            for(LinkMetric metric : task.getLinkMetrics()) {
                metricNames.add(metric.getMetricName());
            }

            agentMetrics.put(entry.getKey(), metricNames);
        }
    }

    private void runMenu() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            this.activeAgent = "";
            clearScreen();
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Escolher Agente");
            System.out.println("2. Estatísticas Gerais da Rede");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            int opt;
            try {
                opt = scanner.nextInt();
            } catch (InputMismatchException e) {
                opt = -1;
            }

            switch (opt) {
                case 1:
                    menuChooseAgent();
                    break;
                case 2:
                    menuMetricOrAlert(true);
                    break;
                case 0:
                    System.out.println("Encerrando o sistema...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void menuChooseAgent() {
        clearScreen();
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- ESCOLHA DE AGENTE ---");
        System.out.print("Introduza o identificador de um agente: ");

        String id = scanner.nextLine();

        if (this.tasks.containsKey(id)) {
            this.activeAgent = id;
            menuMetricOrAlert(false);
        } else {
            System.out.println("Agente não reconhecido!");
        }
    }

    private void menuMetricOrAlert(boolean global) {
        while (true) {
            clearScreen();
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n--- MENU " + (global ? "" : this.activeAgent) + " ---");
            System.out.println("1. Consultar valores das métricas");
            System.out.println("2. Consultar valores dos alertas");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            int opt;
            try {
                opt = scanner.nextInt();
            } catch (InputMismatchException e) {
                opt = -1;
            }

            switch (opt) {
                case 1:
                    if(global) {
                        menuChooseMetric(List.of(MetricName.values()), true);
                    }
                    else{
                        menuChooseMetricQuantity();
                    }
                    break;
                case 2:
                    displayAlerts(global);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void menuChooseMetricQuantity() {
        while (true){
            clearScreen();
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n--- MENU " +  this.activeAgent + " ---");
            System.out.println("1. Consultar métrica individual");
            System.out.println("2. Consultar métricas gerais");
            System.out.println("0. Voltar");

            int opt;
            try {
                opt = scanner.nextInt();
            } catch (InputMismatchException e) {
                opt = -1;
            }

            switch (opt) {
                case 1:
                    menuChooseMetric(agentMetrics.get(this.activeAgent), false);
                    break;
                case 2:
                    displayAllStats();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void menuChooseMetric(List<MetricName> metricNames, boolean global) {
        while (true) {
            clearScreen();
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n--- ESCOLHA TIPO DE MÉTRICA ---");
            int i = 1;
            for(MetricName metricName : metricNames) {
                System.out.println(i + ". " + metricName);
                i++;
            }
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            int opt;
            try {
                opt = scanner.nextInt();
            } catch (InputMismatchException e) {
                opt = -1;
            }

            if (opt == 0) {
                return;
            }
            else if (opt > i - 1 || opt < 0){
                System.out.println("Opção inválida!");
            }
            else {
                displayMetrics(metricNames.get(opt - 1), global);
            }
        }
    }

    private void displayMetrics(MetricName name, boolean global) {
        clearScreen();
        MetricStats stats = storageModule.getMetrics(this.activeAgent, name, global);
        if(global){
            System.out.println("\n--- Stats globais da rede ---");
        } else{
            System.out.println("\n--- Stats de " + this.activeAgent + " ---");
        }

        if(stats == null){
            System.out.println("Sem valores a apresentar.");
        } else {
            System.out.println("\n" + name.toString() + ":");
            if(global){
                System.out.println(stats.toStringStats(true));
            }
            else {
                System.out.println(stats.toStringMeasures());
            }
        }
        waitForEnter();
    }

    private void displayAllStats() {
        clearScreen();
        Set<Map.Entry<MetricName, MetricStats>> stats = storageModule.getAllMetrics(this.activeAgent);
        System.out.println("\n--- Stats de " + this.activeAgent+ " ---\n");

        if(stats.isEmpty()){
            System.out.println("Sem valores a apresentar.");
        } else {
            for (Map.Entry<MetricName, MetricStats> entry : stats) {
                System.out.println(entry.getKey().toString() + ":");
                System.out.println(entry.getValue().toStringStats(true));
            }
        }
        waitForEnter();
    }

    private void displayAlerts(boolean global) {
        clearScreen();
        List<Notification> alerts = storageModule.getAlerts(this.activeAgent, global);
        if(global){
            System.out.println("\n--- Alertas globais da rede ---");
        } else{
            System.out.println("\n--- Alertas de " + this.activeAgent + " ---");
        }

        if(alerts.isEmpty()){
            System.out.println("Sem valores a apresentar.");
        } else {
            for (Notification notification : alerts) {
                System.out.print(notification.toString());
                if(global){
                    System.out.print(" (" + notification.getID() + ")");
                }
                System.out.print("\n");
            }
        }

        waitForEnter();
    }

    private void waitForEnter() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nPressione Enter para continuar... ");
        scanner.nextLine();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {
        NMS_Server server = new NMS_Server();
        server.initialize();
    }
}