package message;

public class Message {
    private int seqNumber;
    private int ackNumber;
    private MessageType type;
    private Data data;

    public Message(int seqNumber, int ackNumber, MessageType type, Data msgData) {
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.type = type;
        this.data = msgData;
    }

    public Message(String[] fields) {
        this.seqNumber = Integer.parseInt(fields[0]);
        this.ackNumber = Integer.parseInt(fields[1]);
        this.type = MessageType.fromInteger(Integer.parseInt(fields[2]));

        switch(this.type){
            case Task:
                this.data = new Task(fields, 3);
                break;
            case TaskResult:
                //TODO
                break;
            case Notification:
                this.data = new Notification(fields, 3);
                break;
            default:
                data = null;
                break;
        }
    }

    public String getPDU(){
        return seqNumber +
                ';' +
                ackNumber +
                ';' +
                type.toInteger() +
                ';' +
                data.getPayload();
    }

    @Override
    public String toString() {
        return "Message{" +
                "seqNumber=" + seqNumber +
                ", ackNumber=" + ackNumber +
                ", type=" + type +
                ", msgData=" + data.toString() +
                '}';
    }
}