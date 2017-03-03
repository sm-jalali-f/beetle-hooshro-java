package client.model;

public class Entity {
    private int id = -1;
    private EntityType entityType;
    private Cell cell;

    Entity() {
    }

    Entity(int id) {
        this.id = id;
    }

    Entity(int id, EntityType entityType) {
        this.id = id;
        this.entityType = entityType;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    void setCell(Cell cell) {
        this.cell = cell;
    }

    public Cell getPosition() {
        return this.cell;
    }

    public EntityType getType() {
        return this.entityType;
    }

    public int getRow(){
        return this.cell.getX();
    }

    public int getColumn(){
        return this.cell.getY();
    }
}