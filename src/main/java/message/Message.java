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

    public Message(byte[] pdu, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(pdu, 0, length);
        this.seqNumber = buffer.getInt();
        this.ackNumber = buffer.getInt();
        this.type = MessageType.fromByte(buffer.get());
        byte[] dataBytes = new byte[buffer.remaining()];
        buffer.get(dataBytes);
        String[] fields = (new String(dataBytes, StandardCharsets.UTF_8)).split(";");

        switch(this.type){
            case Task:
                this.data = new Task(fields);
                break;
            case TaskResult:
                this.data = new TaskResult(fields);
                break;
            case Notification:
                this.data = new Notification(fields);
                break;
            default:
                this.data = null;
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

    public Data getData() {
        return data;
    }

    public byte[] getPDU() {
        ByteBuffer buffer = ByteBuffer.allocate(2 * Integer.BYTES + 1 + (data == null ? 0 : data.getPayload().length()));
        buffer.putInt(seqNumber);
        buffer.putInt(ackNumber);
        buffer.put(type.toByte());
        if(data != null) {
            buffer.put(data.getPayload().getBytes(StandardCharsets.UTF_8));
        }
        return buffer.array();
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