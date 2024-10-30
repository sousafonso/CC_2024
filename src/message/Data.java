/**
 * @description Data class is an abstract class that represents the data that is sent from the server to the client.
 */

package message;

public abstract class Data {
    private String id;
    private String type;
    private String description;
    private String value;
    private String timestamp;

    public Data(String id, String type, String description, String value, String timestamp) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", value='" + value + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
