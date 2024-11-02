package message;

import java.net.InetAddress;

public class Message {
    private InetAddress source;
    private InetAddress destination;
    private int seqNumber;
    private int ackNumber;
    private MessageType type; //TODO talvez substituir por conjunto de bits
    private Data msgData;

    public Message(int seqNumber, int ackNumber, Data msgData, MessageType type) {
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.msgData = msgData;
        this.type = type;
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public int getAckNumber() {
        return ackNumber;
    }

    public Data getMsgData() {
        return msgData;
    }

    public MessageType getType() {
        return type;
    }

    @Override

    public String toString() {
        return "Message{" +
                "seqNumber=" + seqNumber +
                ", ackNumber=" + ackNumber +
                ", msgData=" + msgData +
                ", type=" + type +
                '}';
    }

    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }

    public void setAckNumber(int ackNumber) {
        this.ackNumber = ackNumber;
    }

    public void setMsgData(Data msgData) {
        this.msgData = msgData;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
