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

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import message.*;

public class NMS_Server {
    private final int UDP_PORT = 5000; //TODO talvez mover constantes para a classe NetTaskServerListener
    private final int TCP_PORT = 6000;

    private StorageModule storageModule;
    private JSONTaskReader taskReader;
    //private NetTaskHandler netTaskHandler;
    //private AlertFlowHandler alertFlowHandler;
    

    public NMS_Server() {
        this.storageModule = new StorageModule(); // Inicializa o módulo de armazenamento
        this.taskReader = new JSONTaskReader(); // Inicializa o leitor de tarefas
        //this.netTaskHandler = new NetTaskHandler(5000, storageModule);  // Porta UDP e armazena métricas
        //this.alertFlowHandler = new AlertFlowHandler(5001, storageModule);  // Porta TCP e armazena alertas
    }

    public void initialize() {
        System.out.println("A iniciar servidor");
        //TODO ler json e ter as tarefas todas organizadas (falta classe que representa uma Task para o servidor com os devices)

        // Carregar tarefas do JSON de configuração
        //TODO deixar ip como chave e ser isso o id do device, ou deixar o nome e forncer isso no registo (modificar esse tipo de mensagem)
        Map<String, Task> tasks  = this.taskReader.readJson();
        for(Map.Entry<String, Task> entry: tasks.entrySet()) {
            System.out.println("Device " + entry.getKey() + " : " + entry.getValue() + "\n");
        }

        // Distribuir tarefas para os agentes
        //distributeTasks(tasks);
        Thread NetTaskListener = new Thread(new NetTaskServerListener(UDP_PORT, tasks));
        Thread AlertFlowListener = new Thread(new AlertFlowListener(TCP_PORT, storageModule));
        NetTaskListener.start();
        AlertFlowListener.start();

        //TODO apresentação de métricas na UI

        /*try {
            NetTaskListener.join();
            //AlertFlowListener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        // Iniciar exibição periódica de métricas a cada 10 segundos
        startMetricDisplayScheduler();
    }

    /*private void distributeTasks(List<Task> tasks) {
        if (tasks != null) {
            for (Task task : tasks) {
            System.out.println("A distribuir tarefa: " + task.getTaskId());
                netTaskHandler.sendTaskToAgents(task);  // Enviar a tarefa para NetTaskHandler
            }
        } else {
            System.out.println("Nenhuma tarefa carregada.");
        }
    }*/

    private void distributeTasks(Map<String, Task> tasks) {
        if (tasks != null) {
            for (Map.Entry<String, Task> entry : tasks.entrySet()) {
                System.out.println("A distribuir tarefa: " + entry.getValue().getTaskId());
                //netTaskHandler.sendTaskToAgents(entry.getValue());  // Enviar a tarefa para NetTaskHandler
            }
        } else {
            System.out.println("Nenhuma tarefa carregada.");
        }
    }

    private void startMetricDisplayScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> storageModule.displayMetrics(), 0, 10, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        NMS_Server server = new NMS_Server();
        server.initialize();
    }
}