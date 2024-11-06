package message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MessagePDU {
    private static final int INET_ADDRESS_LENGTH = 4; // IPv4
    private static final int STRING_LENGTH = 256; // Tamanho fixo para a string de dados

    public static byte[] toPDU(Message message) {
        ByteBuffer buffer = ByteBuffer.allocate(2 * INET_ADDRESS_LENGTH + 2 * Integer.BYTES + STRING_LENGTH + 1);

        // Serializando source
        buffer.put(message.getSource().getAddress());

        // Serializando destination
        buffer.put(message.getDestination().getAddress());

        // Serializando seqNumber
        buffer.putInt(message.getSeqNumber());

        // Serializando ackNumber
        buffer.putInt(message.getAckNumber());

        // Serializando type
        buffer.put((byte) message.getType().ordinal());

        // Serializando data
        byte[] dataBytes = message.getMsgData().getBytes(StandardCharsets.UTF_8);
        buffer.put(dataBytes, 0, Math.min(dataBytes.length, STRING_LENGTH));

        return buffer.array();
    }

    public static Message fromPDU(byte[] pdu) {
        ByteBuffer buffer = ByteBuffer.wrap(pdu);

        // Desserializando source
        byte[] sourceBytes = new byte[INET_ADDRESS_LENGTH];
        buffer.get(sourceBytes);
        try {
            InetAddress source = InetAddress.getByAddress(sourceBytes);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Desserializando destination
        byte[] destinationBytes = new byte[INET_ADDRESS_LENGTH];
        buffer.get(destinationBytes);
        try {
            InetAddress destination = InetAddress.getByAddress(destinationBytes);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Desserializando seqNumber
        int seqNumber = buffer.getInt();

        // Desserializando ackNumber
        int ackNumber = buffer.getInt();

        // Desserializando type
        MessageType type = MessageType.values()[buffer.get()];

        // Desserializando data
        byte[] dataBytes = new byte[STRING_LENGTH];
        buffer.get(dataBytes);
        String data = new String(dataBytes, StandardCharsets.UTF_8).trim();

        return new Message(seqNumber, ackNumber, data, type);
    }
}