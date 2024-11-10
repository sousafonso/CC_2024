package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.InetAddress;

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

    //TODO construtor byte -> mensagem

    public byte[] getPDU(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            DataOutputStream os = new DataOutputStream(out);
            os.writeInt(this.seqNumber);
            os.writeInt(this.ackNumber);
            os.writeInt(this.type.toInteger());
            if(data != null) {
                byte[] payload = data.getPayload();
                os.write(payload, 0, payload.length);
            }
            os.flush();
        } catch (IOException e) {
            System.out.println("Erro ao serializar objeto");
        }

        return out.toByteArray();
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