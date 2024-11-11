package message;

import java.io.*;

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

    public Message(Message msg) {
        this.seqNumber = msg.seqNumber;
        this.ackNumber = msg.ackNumber;
        this.type = msg.type;
        this.data = msg.data;

    }

    //TODO construtor byte -> mensagem

    public Message(byte[] serializedMessage) {
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedMessage);
        try{
            DataInputStream dis = new DataInputStream(bais);
            this.seqNumber = dis.readInt();
            this.ackNumber = dis.readInt();
            this.type = MessageType.fromInteger(dis.readInt());
            byte[] dataBytes = new byte[dis.available()];
            dis.read(dataBytes);
            switch(type){
                case Task:
                    Task task = new Task();
                    this.data = task.rebuild(dataBytes);
                    break;
            }
        } catch (IOException e) {
            System.out.println("Mensagem no formato errado");
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