package message;

import java.io.Serializable;
import java.net.InetAddress;

public class Message implements Serializable {
    private InetAddress source;
    private InetAddress destination;
    private int seqNumber;
    private int ackNumber;
    private MessageType type;
    //private Data msgData;
    private String data; //TODO temporariamente só string para simplificar

    public Message(int seqNumber, int ackNumber, String msgData, MessageType type) {
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.data = msgData;
        //this.msgData = msgData;
        this.type = type;
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public int getAckNumber() {
        return ackNumber;
    }

    /*public Data getMsgData() {
        return msgData;
    }*/

    public String getMsgData() {
        return data;
    }

    public MessageType getType() {
        return type;
    }

    @Override

    public String toString() {
        return "Message{" +
                "seqNumber=" + seqNumber +
                ", ackNumber=" + ackNumber +
                ", msgData=" + data +
                ", type=" + type +
                '}';
    }

    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }

    public void setAckNumber(int ackNumber) {
        this.ackNumber = ackNumber;
    }

    public void setMsgData(String msgData) {
        this.data = msgData;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
