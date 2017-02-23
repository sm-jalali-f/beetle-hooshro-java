package client.model;

public class Entity {
    private int id = -1;
    private EntityType entityType;
    private Cell cell;
    /*
     * These two fields are only used when we are
     * passing an Entity object to user
     */
    private int x;
    private int y;

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

    int getX() {
        return x;
    }

    void setX(int x) {
        this.x = x;
    }

    int getY() {
        return y;
    }

    void setY(int y) {
        this.y = y;
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