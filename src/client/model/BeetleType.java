package client.model;

/**
 * Created by Future on 2/10/17.
 */
public enum BeetleType {
    LOW(0), HIGH(1);

    private int value;
    BeetleType(int value) {
        this.value = value;
        BeetleType type;
    }

    public int getValue() {
        return value;
    }
}
