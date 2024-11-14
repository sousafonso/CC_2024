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

    public Message(byte[] pdu) {
        ByteBuffer buffer = ByteBuffer.wrap(pdu);
        this.seqNumber = buffer.getInt();
        this.seqNumber = buffer.getInt();
        this.type = MessageType.fromByte(buffer.get());
        byte[] dataBytes = new byte[buffer.remaining()];
        buffer.get(dataBytes);
        String[] fields = (new String(dataBytes, StandardCharsets.UTF_8)).split(";");

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

    public byte[] getPDU() {
        ByteBuffer buffer = ByteBuffer.allocate(2 * Integer.BYTES + 1 + data.getPayload().length());
        buffer.putInt(seqNumber);
        buffer.putInt(ackNumber);
        buffer.put(type.toByte());
        buffer.put(data.getPayload().getBytes(StandardCharsets.UTF_8));
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