# CC_2024
Desenvolvimento de um sistema de redes distribuído

Objetivo:
O objetivo deste trabalho prático é desenvolver um Sistema de Monitorização de Redes distribuído, composto por um NMS_Server (servidor central) e vários NMS_Agents (agentes de monitorização), que monitoram o estado dos dispositivos de rede e enviam alertas ao servidor quando ocorrem anomalias críticas. O sistema precisa ser resiliente e capaz de operar num ambiente com falhas ou degradação da rede.

Requisitos Técnicos:
1.	Monitorização Distribuída: O sistema deve ser implementado de forma distribuída, com os NMS_Agents a recolher métricas de dispositivos de rede reportando ao NMS_Server.
2.	Modelos de Comunicação Cliente-Servidor:
    o	NMS_Agents: Responsáveis por recolher as métricas dos dispositivos de rede.
    o	NMS_Server: Responsável por coordenar as atividades dos agentes e armazenar as métricas.
3.	Protocolos a Desenvolver:
    o	NetTask (UDP): Utilizado para a recolha contínua de métricas pelos agentes e envio ao servidor.
    o	AlertFlow (TCP): Utilizado para notificar o servidor em caso de detecção de falhas ou anomalias.
4.	Métricas de Rede a Monitorizar:
    o	Uso de CPU e RAM.
    o	Estatísticas das interfaces de rede.
    o	Largura de banda, latência, jitter e perda de pacotes (utilizando ferramentas como ping e iperf).
5.	Sistema de Alertas: Quando métricas ultrapassam limites pré-configurados (definidos no ficheiro JSON), os agentes enviam alertas críticos via TCP.
6.	Ficheiro de Configuração (JSON):
    o	Contém as tarefas a serem atribuídas aos agentes, definindo quais dispositivos monitorar, quais as métricas a recolher e com que frequência.
7.	Simulação e Ambiente de Testes:
    o	O trabalho deve ser testado em um ambiente simulado, utilizando o emulador de redes CORE 7.5, que permite a criação de uma topologia de rede para validar a solução.
8.	Resiliência: O sistema deve implementar mecanismos que garantam a robustez em condições de rede instáveis, incluindo retransmissão de pacotes perdidos e controle de fluxo.
 
1.	NetTask (Protocolo UDP)

O NetTask será o protocolo utilizado para a comunicação principal entre os NMS_Agents e o NMS_Server. Este protocolo usará UDP como camada de transporte para otimizar a velocidade de envio e recebimento de métricas. No entanto, como o UDP não garante a entrega de pacotes, serão implementados mecanismos adicionais para garantir a confiabilidade.

Formato da Mensagem NetTask:
Cada mensagem enviada via NetTask deve seguir um formato claro e estruturado. Abaixo está uma proposta de formato:

Mensagem de Registro do NMS_Agent:

{
  "agent_id": "agent_1",
  "task_request": true
}

Resposta do NMS_Server com Tarefa:

{
  "task_id": "task_X",
  "metrics": {
    "cpu_usage": true,
    "ram_usage": true,
    "interface_stats": ["eth0", "eth1"]
  },
  "frequency": 30
}

O servidor envia a tarefa ao agente, especificando as métricas que precisam de ser recolhidas e a frequência da recolha.

Envio de Métricas pelo NMS_Agent:
{
  "agent_id": "agent_X",
  "task_id": "task_Y",
  "metrics": {
    "cpu_usage": 45.6,
    "ram_usage": 68.2,
    "interface_stats": {
      "eth0": {"packets_sent": 1200, "packets_received": 1150},
      "eth1": {"packets_sent": 800, "packets_received": 780}
    }
  }
}
 
Mecanismos de Controlo para NetTask (UDP):
•	Números de Sequência e Acknowledgment (ACK):
    o	Cada mensagem enviada via NetTask incluirá um número de sequência único, para garantir que o servidor possa reordenar as mensagens e detectar pacotes perdidos.
    o	O NMS_Server deve enviar um ACK para cada mensagem recebida, confirmando seu recebimento. Se o NMS_Agent não receber o ACK, ele retransmitirá a mensagem.
•	Retransmissão:
    o	Se um pacote for perdido, o NMS_Agent retransmitirá as métricas.
•	Controlo de Fluxo (Opcional):
    o	O agente deve limitar a frequência de envio de métricas para evitar sobrecarregar o servidor ou a rede.

2.	AlertFlow (Protocolo TCP)

O AlertFlow será o protocolo utilizado para a comunicação de alertas críticos. Ele utilizará TCP para garantir a entrega confiável das mensagens de alerta. O TCP é adequado aqui porque os alertas são eventos importantes, e a confiabilidade da entrega é crucial.
Formato da Mensagem AlertFlow:
•	Mensagem de Alerta do NMS_Agent:
{
  "agent_id": "agent_1",
  "alert_type": "cpu_usage",
  "alert_value": 90.5,
  "threshold": 80,
  "timestamp": "2024-10-16T12:34:56"
}

O agente envia um alerta ao servidor a indicar que o uso de CPU ultrapassou o limite estabelecido (80%).

Mecanismos de Controlo para AlertFlow (TCP):
•	O TCP já oferece controlo de fluxo, entrega garantida e ordenação de pacotes, portanto não são necessários mecanismos adicionais de controlo no protocolo AlertFlow.

