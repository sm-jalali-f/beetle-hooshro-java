package client.freezAi.models;

/**
 * Created by mohamad on 2/24/17.
 */
public enum BeetleStrategy {
    EatFood(0),SlipperBreak(1),TrashBreak(2),EnemyBreak(3),EnemyFollow(4),Nothing(5);
    private int value;

    BeetleStrategy(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
