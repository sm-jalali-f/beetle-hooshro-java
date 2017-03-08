package client.model;

import com.google.gson.JsonArray;

public class Cell {
    private int x;
    private int y;
    private Entity beetleEntity;
    private Entity slipperEntity;
    private Entity teleportEntity;
    private Entity foodEntity;
    private Entity trashEntity;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }


    void addTrash(int id, int trashValidTime) {
        trashEntity = new Trash(id, trashValidTime);
    }


    void addFood(int id, int foodValidTime) {
        foodEntity = new Food(id, foodValidTime);
    }


    void addSlipper(int id, int slipperValidTime) {
        slipperEntity = new Slipper(id, slipperValidTime);
    }

    void addTeleport(int id, int targetId) {
        teleportEntity = new Teleport(id, targetId);
    }

    void receiveInfo(Entity entity) {
        if (entity instanceof Beetle) {
            beetleEntity = entity;
        } else if (entity instanceof Food) {
            foodEntity = entity;
        } else if (entity instanceof Trash) {
            trashEntity = entity;
        } else if (entity instanceof Slipper) {
            slipperEntity = entity;
        } else if (entity instanceof Teleport) {
            teleportEntity = entity;
        }
    }

    void addBeetleInfo(JsonArray beetleInfo) {
        beetleEntity = new Beetle(beetleInfo.get(0).getAsInt());
        Beetle chosenBeetleInfo = (Beetle) beetleEntity;
        chosenBeetleInfo.setDirection(beetleInfo.get(3).getAsInt());
        chosenBeetleInfo.setColor(beetleInfo.get(4).getAsInt());
        chosenBeetleInfo.setQueen(beetleInfo.get(5).getAsInt());
        chosenBeetleInfo.setSick(beetleInfo.get(6).getAsInt());
        chosenBeetleInfo.setTeam(beetleInfo.get(7).getAsInt());
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    void addBeetleInfo(int id, int direction, int color, int queen, int team) {
        beetleEntity = new Beetle(id);
        Beetle chosenBeetleInfo = (Beetle) beetleEntity;
        chosenBeetleInfo.setDirection(direction);
        chosenBeetleInfo.setColor(color);
        chosenBeetleInfo.setQueen(queen);
        chosenBeetleInfo.setSick(0);
        chosenBeetleInfo.setTeam(team);
    }

    void clear() {
        beetleEntity = null;
        foodEntity = null;
        trashEntity = null;
    }

    void cleanSlipper() {
        slipperEntity = null;
    }

    public Entity getBeetle() {
        return beetleEntity;
    }

    void setBeetleEntity(Entity beetleEntity) {
        this.beetleEntity = beetleEntity;
    }

    public Entity getItem() {
        if (foodEntity != null) {
            return foodEntity;
        } else {
            return trashEntity;
        }
    }


    void setItemEntity(Entity itemEntity) {
        if (itemEntity instanceof Food) {
            this.foodEntity = itemEntity;
        } else {
            this.trashEntity = itemEntity;
        }
    }

    public Entity getSlipper() {
        return slipperEntity;
    }

    void setSlipperEntity(Entity slipperEntity) {
        this.slipperEntity = slipperEntity;
    }

    public Entity getTeleport() {
        return teleportEntity;
    }

    void setTeleportEntity(Entity teleportEntity) {
        this.teleportEntity = teleportEntity;
    }

    Entity getFoodEntity() {
        return foodEntity;
    }

    void setFoodEntity(Entity foodEntity) {
        this.foodEntity = foodEntity;
    }

    public Entity getTrashEntity() {
        return trashEntity;
    }

    void setTrashEntity(Entity trashEntity) {
        this.trashEntity = trashEntity;
    }
}