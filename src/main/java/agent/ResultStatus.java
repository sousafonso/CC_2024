package agent;

import message.Message;

import java.time.LocalDateTime;

public class ResultStatus {
    private final Message message;
    private LocalDateTime timeSent;
    private int tries;

    public ResultStatus(LocalDateTime timeSent, Message message) {
        this.timeSent = timeSent;
        this.message = message;
        this.tries = 0;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        ResultStatus other = (ResultStatus) obj;
        return this.timeSent.equals(other.timeSent) && this.message.equals(other.message) && this.tries == other.tries;
    }
}
