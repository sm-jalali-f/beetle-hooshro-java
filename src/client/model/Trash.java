package client.model;

/**
 * Created by Future on 2/10/17.
 */
public class Trash extends Entity {
    private int remainingTurn;

    Trash(int id, int trashValidTime) {
        super(id, EntityType.Trash);
        this.remainingTurn = trashValidTime--;
    }

    public int getRemainingTurns() {
        return this.remainingTurn;
    }

    void setRemainingTurn(int remainingTurn) {
        this.remainingTurn = remainingTurn;
    }
}
