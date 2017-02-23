package client.model;

public class Slipper extends Entity {
    private int remainingTurn;

    Slipper(int id, int slipperValidTime) {
        super(id, EntityType.Slipper);
        remainingTurn = slipperValidTime--;
    }

    public int getRemainingTurns() {
        return this.remainingTurn;
    }

    void setRemainingTurn(int remainingTurn) {
        this.remainingTurn = remainingTurn;
    }
}