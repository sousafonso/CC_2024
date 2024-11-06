package message;

import java.net.InetAddress;

public class Message {
    private InetAddress source;
    private InetAddress destination;
    private int seqNumber;
    private int ackNumber;
    private MessageType type;
    private String data;

    public Message(int seqNumber, int ackNumber, String msgData, MessageType type) {
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.data = msgData;
        this.type = type;
    }

    public InetAddress getSource() {
        return source;
    }

    public void setSource(InetAddress source) {
        this.source = source;
    }

    public InetAddress getDestination() {
        return destination;
    }

    public void setDestination(InetAddress destination) {
        this.destination = destination;
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public int getAckNumber() {
        return ackNumber;
    }

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
}