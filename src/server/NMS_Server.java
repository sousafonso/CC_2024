/**
 * NMS_Server.java
 * @description O servidor será responsável por coordenar as atividades dos NMS_Agents registados e estará dividido
nos seguintes requisitos funcionais:
    1. Interpretação de Tarefas: Processar tarefas descritas num ficheiro JSON, que serão enviadas aos
NMS_Agents. A tarefa especifica que métricas devem ser coletadas e com que frequência. Também
identifica quais dados devem ser monitorizados.
    2. Apresentação de Métricas: Oferecer uma interface para consultar as métricas recolhidas pelos
NMS_Agents.
    3. Comunicação UDP (NetTask): Comunicar com os NMS_Agents através de UDP, enviando
tarefas e recebendo as métricas coletadas.
    4. Comunicação TCP (AlertFlow): Receber notificações de alterações críticas nos dados
monitorizados.
    5. Armazenamento de Dados: Armazenar todas as métricas e informações recebidas dos
NMS_Agents

    O NMS_Server recebe um ficheiro JSON que descreve as métricas a serem coletadas para cada dispositivo, bem
como os limites que, ao serem ultrapassados, desencadeiam a comunicação via AlertFlow. É fundamental que
o NMS_Server interprete corretamente o arquivo, de modo a atribuir a tarefa adequada a cada NMS_Agent,
garantindo assim uma monitorização eficiente e a resposta imediata a eventuais problemas.

  * @date december 2024
 */
package server;

public class NMS_Server {
    private static final int UDP_PORT = 5000; // Porta (à escolha) UDP para comunicação com os NMS_Agents 
    private static final int TCP_PORT = 6000; // Porta (à escolha) TCP para comunicação com os AlertFlows

    public static void main(String[] args) throws InterruptedException {
        System.out.println("NMS Server iniciado.");
        
        Thread NetTaskLisener = new Thread(new NetTaskListener(UDP_PORT));
        Thread AlertFlowListener = new Thread(new AlertFlowListener(TCP_PORT));

        NetTaskLisener.start();
        AlertFlowListener.start();

        try {
            NetTaskLisener.join();
            AlertFlowListener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        /*
        // Leitura do ficheiro JSON
        JSONTaskReader jsonTaskReader = new JSONTaskReader();
        jsonTaskReader.readConfigFile("config/config.json");

        for (Device device : jsonTaskReader.getDevices()) {
            // Inicialização de um NMS_Agent para cada dispositivo
            NMS_Agent agent = new NMS_Agent(device.getDeviceId(), UDP_PORT);
            agent.start();

            // Inicialização de um AlertFlow para cada dispositivo
            AlertFlow alertFlow = new AlertFlow(device.getDeviceId(), TCP_PORT);
            alertFlow.start();

            // Envio da tarefa para o NMS_Agent
            agent.sendTask(jsonTaskReader.getTaskId(), jsonTaskReader.getFrequency(), device.getDeviceMetrics());
            
            // Envio dos limites para o AlertFlow
            alertFlow.sendLimits(device.getDeviceMetrics().getLimits());

            System.out.println("NMS Agent e AlertFlow iniciados para o dispositivo " + device.getDeviceId());

            // Aguardar 1 segundo antes de iniciar o próximo NMS_Agent
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
        System.out.println("NMS Server terminado.");
    }
}