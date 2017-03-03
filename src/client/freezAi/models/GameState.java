package client.freezAi.models;

import client.World;
import client.freezAi.Utility;
import client.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mohamad on 2/23/17.
 */
public class GameState {

    double cost;
    double heutristic;
    HashMap<Integer,Food> foodList;
    HashMap<Integer,Integer> foodAgeList;
    HashMap<Integer,Trash> trashList;
    HashMap<Integer,Integer> trashAgeList;
    HashMap<Integer,Slipper> slipperList;
    HashMap<Integer,Integer> slipperAgeList;
    int beetlePosX,beetlePosY;
    Direction beetleDir;
    int beetlePower;
    World game;
    int mapWidth,mapHeight;
    GameState parentState;
    double f;
    public GameState(World game,Direction beetleDir,int beetlePosX,int beetlePosY,int beetlePower
            ,HashMap<Integer,Food> foodMap,HashMap<Integer,Integer>foodAgeMap,
            HashMap<Integer,Trash> trashMap,HashMap<Integer,Integer>trashAgeMap,
            HashMap<Integer,Slipper> slipperMap,HashMap<Integer,Integer>slipperAgeMap
            ,GameState parent) {
        this.parentState = parent;

        this.game = game;
        this.beetleDir =beetleDir;
        this.beetlePosX =beetlePosX;
        this.beetlePosY =beetlePosY;
        this.beetlePower = beetlePower;
        this.mapWidth = game.getMap().getWidth();
        this.mapHeight = game.getMap().getHeight();
        this.foodList=new HashMap<>();
        this.foodAgeList=new HashMap<>();
        this.trashList=new HashMap<>();
        this.trashAgeList=new HashMap<>();
        this.slipperList=new HashMap<>();
        this.slipperAgeList=new HashMap<>();

        for (Integer key:slipperMap.keySet()){
            this.slipperList.put(key,slipperMap.get(key));
        }

        for (Integer key:slipperAgeMap.keySet()){
            this.slipperAgeList.put(key,slipperAgeMap.get(key));
        }

        for (Integer key:trashMap.keySet()){
            this.trashList.put(key,trashMap.get(key));
        }
        for (Integer key:trashAgeMap.keySet()){
            this.trashAgeList.put(key,trashAgeMap.get(key));
        }
        for (Integer key:foodAgeMap.keySet()){
            this.foodAgeList.put(key,foodAgeMap.get(key));
        }
        for (Integer key:foodMap.keySet()){
            this.foodList.put(key,foodMap.get(key));
        }
        this.heutristic = calculate_manhatan_distance_with_food();
        this.cost =0;
        this.f = this.getCost() +this.getHeutristic();

    }


    public boolean  moveForward(){
        int tempX=beetlePosX;
        int tempY = beetlePosY;
        if(beetleDir==Direction.Up){
            tempX -=1;
        }else if(beetleDir==Direction.Right){
            tempY+=1;
        }else if(beetleDir==Direction.Down){
            tempX+=1;
        }else if(beetleDir==Direction.Left){
            tempY-=1;
        }
        if(tempX==-1){
            tempX = mapHeight-1;
        }else if(tempX==mapHeight){
            tempX=0;
        }
        if(tempY == -1){
            tempY = mapWidth-1;
        }else if(tempY==mapWidth){
            tempY = 0;
        }
        int teleportId = isInTeleport(tempX,tempY);
        if(teleportId!=-1){
            tempX = ((Teleport)game.getMap().getEntity(teleportId)).getPosition().getX();
            tempY = ((Teleport)game.getMap().getEntity(teleportId)).getPosition().getY();
        }
        for(Integer key:trashList.keySet()){
            if(trashList.get(key).getPosition().getX() == tempX && trashList.get(key).getPosition().getY() == tempY ){
                return false;
            }
        }
        for(Integer key:slipperList.keySet()){
            Slipper slipp = slipperList.get(key);
            if(Utility.isUnderSlipper(new Cell(tempX,tempY),new Cell(slipp.getPosition().getX()
                    ,slipp.getPosition().getY()),mapHeight,mapWidth)){
                return false;
            }
        }
        this.cost+=1;
        this.beetlePower+=1;
        this.beetlePosX =tempX;
        this.beetlePosY = tempY;
        updateFoodList();
        updateTrashList();
        updateSlipperList();
        this.heutristic = calculate_manhatan_distance_with_food();
        return true;
    }



    public void turnLeft(){
        switch (beetleDir){
            case Up:
                beetleDir =Direction.Left;
                break;
            case Right:
                beetleDir =Direction.Up;
                break;
            case Down:
                beetleDir =Direction.Right;
                break;
            case Left:
                beetleDir =Direction.Down;
                break;
        }
        int teleportId = isInTeleport(beetlePosX,beetlePosY);
        if(teleportId!=-1){
            beetlePosX = ((Teleport)game.getMap().getEntity(teleportId)).getPosition().getX();
            beetlePosY = ((Teleport)game.getMap().getEntity(teleportId)).getPosition().getY();
        }
        this.cost+=2;
        updateFoodList();
        updateTrashList();
        updateSlipperList();
        this.heutristic = calculate_manhatan_distance_with_food();
    }
    public void turnRight(){
        switch (beetleDir){
            case Up:
                beetleDir =Direction.Right;
                break;
            case Right:
                beetleDir =Direction.Down;
                break;
            case Down:
                beetleDir =Direction.Left;
                break;
            case Left:
                beetleDir =Direction.Up;
                break;
        }
        int teleportId = isInTeleport(beetlePosX,beetlePosY);
        if(teleportId!=-1){
            beetlePosX = ((Teleport)game.getMap().getEntity(teleportId)).getPosition().getX();
            beetlePosY = ((Teleport)game.getMap().getEntity(teleportId)).getPosition().getY();
        }
        this.cost+=2;
        updateFoodList();
        updateTrashList();
        updateSlipperList();
        this.heutristic = calculate_manhatan_distance_with_food();

    }
    public void printFood(){
        for (Integer key:foodAgeList.keySet()){
//            System.out.println("GameState.printFood food.age= "+foodAgeList.get(key));
        }
    }
    private void updateSlipperList() {
        List<Integer> removedSlipperList = new ArrayList<>();
        for(Integer key:slipperList.keySet()){
            int newValue =slipperAgeList.get(key)-1;
            if( newValue == 0 ) {
                removedSlipperList.add(key);
            }
            else
                slipperAgeList.put(key,newValue);
        }
        for (int i = 0; i < removedSlipperList.size(); i++) {
            slipperList.remove(removedSlipperList.get(i));
            slipperAgeList.remove(removedSlipperList.get(i));
        }
    }
    private void updateFoodList(){
        List<Integer> removedFoodKey = new ArrayList<>();
        for(Integer key:foodList.keySet()){
            int newValue =foodAgeList.get(key)-1;
            if( newValue == 0 ) {
                removedFoodKey.add(key);
            }
            else
                foodAgeList.put(key,newValue);
        }
        for (int i = 0; i < removedFoodKey.size(); i++) {
            foodList.remove(removedFoodKey.get(i));
            foodAgeList.remove(removedFoodKey.get(i));
        }
    }
    private void updateTrashList(){
        List<Integer> removedTrashList = new ArrayList<>();
        for(Integer key:trashList.keySet()){
            int newValue =trashAgeList.get(key)-1;
            if( newValue == 0 ) {
                removedTrashList.add(key);
            }
            else
                trashAgeList.put(key,newValue);
        }
        for (int i = 0; i < removedTrashList.size(); i++) {
            foodList.remove(removedTrashList.get(i));
            foodAgeList.remove(removedTrashList.get(i));
        }
    }
    private double calculate_manhatan_distance_with_food(){
        int bestDistance = 5*(mapHeight+mapWidth);
//        System.out.println("GameState.calculate_manhatan_distance_with_food: foodList.size: "+foodList.size());
        for (Integer key:foodList.keySet()){
            int diffX = Math.abs(beetlePosX - foodList.get(key).getPosition().getX());
            diffX = Math.min(diffX,mapHeight-diffX);
            int diffY = Math.abs(beetlePosY - foodList.get(key).getPosition().getY());
            diffY = Math.min(diffY,mapWidth-diffY);
            int distance = diffX+diffY;
            if(distance<bestDistance)
                bestDistance = distance;
        }

        return bestDistance;
    }

    // return id of dest of teleport if not exist in teleport return -1
    public int isInTeleport(int x,int y){
        for (int i = 0; i < game.getMap().getTeleportCells().length; i++) {
            if(game.getMap().getTeleportCells()[0].getX()==x && game.getMap().getTeleportCells()[0].getY()==y){
                return ((Teleport)game.getMap().getTeleportCells()[0].getTeleport()).getPair().getId();
            }
        }
        return -1;
    }
    public String  getKey(){
        return String.valueOf(beetlePosX)+String.valueOf(beetlePosY)+ String.valueOf(beetleDir.getValue());
    }

    public double getCost() {
        return cost;
    }

    public double getHeutristic() {
        return heutristic;
    }

    public HashMap<Integer, Food> getFoodList() {
        return foodList;
    }

    public HashMap<Integer, Integer> getFoodAgeList() {
        return foodAgeList;
    }

    public int getBeetlePosX() {
        return beetlePosX;
    }

    public int getBeetlePosY() {
        return beetlePosY;
    }

    public Direction getBeetleDir() {
        return beetleDir;
    }

    public int getBeetlePower() {
        return beetlePower;
    }

    public World getGame() {
        return game;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    public GameState getParent(){
        return  this.parentState;
    }
    public void setParent(GameState state) {
        this.parentState =state;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public HashMap<Integer, Trash> getTrashList() {
        return trashList;
    }

    public HashMap<Integer, Integer> getTrashAgeList() {
        return trashAgeList;
    }

    public HashMap<Integer, Slipper> getSlipperList() {
        return slipperList;
    }

    public HashMap<Integer, Integer> getSlipperAgeList() {
        return slipperAgeList;
    }
}
