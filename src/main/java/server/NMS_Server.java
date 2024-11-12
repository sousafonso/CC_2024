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

public class NMS_Server {
    private final int UDP_PORT = 5000; //TODO talvez mover constantes para a classe NetTaskServerListener
    private final int TCP_PORT = 6000;

    //private StorageModule storageModule;
    private JSONTaskReader taskReader;
    //private NetTaskHandler netTaskHandler;
    //private AlertFlowHandler alertFlowHandler;
    

    public NMS_Server() {
        //this.storageModule = new StorageModule(); // Inicializa o módulo de armazenamento
        this.taskReader = new JSONTaskReader(); // Inicializa o leitor de tarefas
        //this.netTaskHandler = new NetTaskHandler(5000, storageModule);  // Porta UDP e armazena métricas
        //this.alertFlowHandler = new AlertFlowHandler(5001, storageModule);  // Porta TCP e armazena alertas
    }

    public void initialize() {
        System.out.println("A iniciar servidor");
        //TODO ler json e ter as tarefas todas organizadas (falta classe que representa uma Task para o servidor com os devices)

        // Carregar tarefas do JSON de configuração
        //List<Task> tasks = taskReader.readConfigFile("config/config.json");

        // Distribuir tarefas para os agentes
        //distributeTasks(tasks);
        Thread NetTaskListener = new Thread(new NetTaskServerListener(UDP_PORT));
        //Thread AlertFlowListener = new Thread(new AlertFlowListener(TCP_PORT));
        NetTaskListener.start();
        //AlertFlowListener.start();

        //TODO apresentação de métricas na UI

        /*try {
            NetTaskListener.join();
            //AlertFlowListener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        // Iniciar exibição periódica de métricas a cada 10 segundos
        //startMetricDisplayScheduler();
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

    /*private void startMetricDisplayScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> storageModule.displayMetrics(), 0, 10, TimeUnit.SECONDS);
    }*/

    public static void main(String[] args) {
        System.out.println("A iniciar servidor");
        NMS_Server server = new NMS_Server();
        server.initialize();
    }
}