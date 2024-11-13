package message;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
                this.data = new TaskResult(fields, 3);
                break;
            case Notification:
                this.data = new Notification(fields, 3);
                break;
            default:
                data = null;
                break;
        }
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public int getAckNumber() {
        return ackNumber;
    }

    public MessageType getType() {
        return type;
    }

    // public String getPDU(){
    //     return seqNumber +
    //             ';' +
    //             ackNumber +
    //             ';' +
    //             type.toInteger() +
    //             ';' +
    //             data.getPayload();
    // }

    public byte[] getPDU() {
        ByteBuffer buffer = ByteBuffer.allocate(2 * Integer.BYTES + 1 + data.getPayload().length());
        buffer.putInt(seqNumber);
        buffer.putInt(ackNumber);
        buffer.put((byte) type.ordinal());
        buffer.put(data.getPayload().getBytes(StandardCharsets.UTF_8));
        return buffer.array();
    }

    public static Message fromPDU(byte[] pdu) {
        ByteBuffer buffer = ByteBuffer.wrap(pdu);
        int seqNumber = buffer.getInt();
        int ackNumber = buffer.getInt();
        MessageType type = MessageType.values()[buffer.get()];
        byte[] dataBytes = new byte[buffer.remaining()];
        buffer.get(dataBytes);
        String dataString = new String(dataBytes, StandardCharsets.UTF_8);
        Data data = new Data(dataString); // Ajustar conforme necessário para diferentes tipos de Data (que já nao está a ser utilizada)
        return new Message(seqNumber, ackNumber, type, data);
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