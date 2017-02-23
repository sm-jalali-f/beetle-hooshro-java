package client.model;

/**
 * Created by Future on 2/10/17.
 */
public enum Direction {
    Right(0), Up(1), Left(2), Down(3);

    private int value;
    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
