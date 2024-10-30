/**
 * @description Classe principal do servidor que inicia o servidor. O servidor  é responsável por carregar as
 * tarefas de configuração a partir de um arquivo JSON, distribuir as tarefas para os agentes e iniciar os listeners
 * de rede para receber mensagens dos agentes.
 */

package server;

import java.util.List;

public class NMS_Server {
    private JSONTaskReader taskReader;
    private NetTaskHandler netTaskHandler;
    private AlertFlowHandler alertFlowHandler;

    public NMS_Server() {
        this.taskReader = new JSONTaskReader();
        this.netTaskHandler = new NetTaskHandler(5000);  // Porta UDP para NetTask
        this.alertFlowHandler = new AlertFlowHandler(5001);  // Porta TCP para AlertFlow
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
    }

    private void distributeTasks(List<Task> tasks) {
        if (tasks != null) {
            for (Task task : tasks) {
                System.out.println("A distruibuir tarefa: " + task.getTaskId());
                netTaskHandler.sendTaskToAgents(task);  // Enviar a tarefa para NetTaskHandler
            }
        } else {
            System.out.println("Nenhuma tarefa carregada.");
        }
    }

    public static void main(String[] args) {
        NMS_Server server = new NMS_Server();
        server.initialize();
    }
}