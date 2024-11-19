package message;

public class AgentRegister extends Data{
    private String agentId;

    public AgentRegister(String agentId) {
        this.agentId = agentId;
    }

    public AgentRegister(String[] fields, int startIndex) {
        this.agentId = fields[startIndex];
    }

    public String getAgentId() {
        return agentId;
    }

    public String getPayload(){
        return agentId;
    }
}
