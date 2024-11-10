package message;
public enum MessageType {
    Regist,
    Task,
    TaskResult,
    Notification,
    Ack;
    // TODO fim de conexão ?

    public MessageType fromInteger(int x) {
        return switch (x) {
            case 0 -> Regist;
            case 1 -> Task;
            case 2 -> TaskResult;
            case 3 -> Notification;
            case 4 -> Ack;
            default -> null;
        };
    }

    public int toInteger() {
        return switch (this) {
            case Regist -> 0;
            case Task -> 1;
            case TaskResult -> 2;
            case Notification -> 3;
            case Ack -> 4;
        };
    }
}