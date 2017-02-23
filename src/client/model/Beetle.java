package client.model;

public class Beetle extends Entity {
    private Direction direction;
    private BeetleType beetleType;
    private int directionInt = -1;
    private int color = -1;
    private int queen = -1;
    private int sick = -1;
    private int team = -1;
    private int power = 0;


    Beetle(int id) {
        super(id, EntityType.Beetle);
    }

    public int getPower() {
        return power;
    }

    void setPower(int power) {
        this.power = power;
    }

    int getColor() {
        return color;
    }

    void setColor(int color) {
        switch (color) {
            case 0:
                this.beetleType = BeetleType.LOW;
                break;
            case 1:
                this.beetleType = BeetleType.HIGH;
                break;
        }
        this.color = color;
    }

    int getDirectionInt() {
        return this.directionInt;
    }

    void setQueen(int queen) {
        this.queen = queen;
    }

    void setSick(int sick) {
        this.sick = sick;
    }

    int getTeam() {
        return team;
    }

    void setTeam(int team) {
        this.team = team;
    }

    public boolean is_sick() {
        return (sick == 1);
    }

    public int getRow() {
        return this.getX();
    }

    public int getColumn() {
        return this.getY();
    }

    public Direction getDirection() {
        return this.direction;
    }

    void setDirection(int direction) {
        switch (direction) {
            case 0:
                this.direction = Direction.Right;
                break;
            case 1:
                this.direction = Direction.Up;
                break;
            case 2:
                this.direction = Direction.Left;
                break;
            case 3:
                this.direction = Direction.Down;
                break;
        }
        this.directionInt = direction;
    }

    public BeetleType getBeetleType() {
        return this.beetleType;
    }

    public boolean has_winge() {
        return (queen == 1);
    }


}