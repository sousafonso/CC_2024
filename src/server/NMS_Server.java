import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NMS_Server {
    private StorageModule storageModule;
    private JSONTaskReader taskReader;
    private NetTaskHandler netTaskHandler;
    private AlertFlowHandler alertFlowHandler;

    public NMS_Server() {
        this.storageModule = new StorageModule(); // Inicializa o módulo de armazenamento
        this.taskReader = new JSONTaskReader(); // Inicializa o leitor de tarefas
        this.netTaskHandler = new NetTaskHandler(5000, storageModule);  // Porta UDP e armazena métricas
        this.alertFlowHandler = new AlertFlowHandler(5001, storageModule);  // Porta TCP e armazena alertas
    }

    public void initialize() {
        System.out.println("A iniciar servidor");

        // Carregar tarefas do JSON de configuração
        List<Task> tasks = taskReader.readConfigFile("config/config.json");

        // Distribuir tarefas para os agentes
        distributeTasks(tasks);

        // Inicializar os listeners de rede em threads separadas que servem para receber mensagens dos agentes
        new Thread(netTaskHandler).start();
        new Thread(alertFlowHandler).start();

        // Iniciar exibição periódica de métricas a cada 10 segundos
        startMetricDisplayScheduler();
    }

    private void distributeTasks(List<Task> tasks) {
        if (tasks != null) {
            for (Task task : tasks) {
                System.out.println("A distribuir tarefa: " + task.getTaskId());
                netTaskHandler.sendTaskToAgents(task);  // Enviar a tarefa para NetTaskHandler
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