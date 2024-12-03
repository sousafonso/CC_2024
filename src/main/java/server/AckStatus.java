package server;

import message.Message;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class AckStatus {
    private final Message message;
    private final InetAddress sourceAddress;
    private final int sourcePort;
    private LocalDateTime timeSent;
    private int tries;

    public AckStatus(LocalDateTime timeSent, Message message, InetAddress sourceAddress, int sourcePort) {
        this.timeSent = timeSent;
        this.message = message;
        this.tries = 0;
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(LocalDateTime timeSent) {
        this.timeSent = timeSent;
    }

    public Message getMessage() {
        return message;
    }

    public int getTries() {
        return tries;
    }

    public void incTries() {
        this.tries++;
    }

    public InetAddress getSourceAddress() {
        return sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        AckStatus other = (AckStatus) obj;
        return this.timeSent.equals(other.timeSent) && this.message.equals(other.message) && this.sourceAddress.equals(other.sourceAddress) && this.sourcePort == other.sourcePort;
    }
}

