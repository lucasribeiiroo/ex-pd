package client;

public class WriteRequest {
    public String getValue() {
        return value;
    }

    public WriteRequest(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

    @Override
    public String toString() {
        return "{" +
                "\"value\":\"" + value + '\"' +
                '}';
    }
}
