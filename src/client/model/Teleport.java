package client.model;

public class Teleport extends Entity {
    protected int targetId;
    private Teleport pair;

    Teleport(int id, int targetId) {
        super(id, EntityType.Teleport);
        this.targetId = targetId;
    }

    int getTargetId() {
        return targetId;
    }

    void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public Teleport getPair() {
        return pair;
    }

    void setPair(Teleport pair) {
        this.pair = pair;
    }


}