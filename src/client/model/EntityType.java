package client.model;

/**
 * Created by Future on 2/10/17.
 */
public enum EntityType {
    Beetle(0), Food(1), Trash(2), Slipper(3), Teleport(4);

    private int value;
    EntityType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
