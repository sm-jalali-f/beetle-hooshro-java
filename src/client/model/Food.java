package client.model;

/**
 * Created by Future on 2/10/17.
 */
public class Food extends Entity {
    private int remainingTurn;

    Food(int id, int foodValidTime) {
        super(id, EntityType.Food);
        remainingTurn = foodValidTime--;
    }

    public int getRemainingTurns() {
        return remainingTurn;
    }

    void setRemainingTurn(int remainingTurn) {
        this.remainingTurn = remainingTurn;
    }
}
