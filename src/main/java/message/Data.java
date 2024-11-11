/**
 * @description Data class is an abstract class that represents the data that is sent from the server to the client.
 */

package message;

public abstract class Data {
    //private String id;
    //private String type;
    //private String description;
    //private String value;
    //private String timestamp;
    //private int size;

    /*public Data(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }*/

    public abstract String getPayload();
}
