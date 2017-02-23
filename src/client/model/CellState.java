package client.model;

/**
 * Created by Future on 2/10/17.
 */
public enum CellState {
    Ally(0), Enemy(1), Blank(2);

    private int value;
    CellState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
