/**
 * @description Data class is an abstract class that represents the data that is sent from the server to the client.
 */

package message;

public abstract class Data {
    //private String timestamp;

    /*public Data(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }*/

    public abstract String getPayload();
}
