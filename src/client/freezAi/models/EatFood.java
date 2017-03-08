package client.freezAi.models;

import client.World;
import client.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mohamad on 3/8/17.
 */
public class EatFood {

    Beetle atackerBeetle;
//    Food goalFood;
    AttackGameState eater;
    World game;
    public EatFood(World game, Beetle btl) {
        this.game =game;
        this.atackerBeetle =btl;
        HashMap<Integer,Food> foodList = new HashMap<>();
        HashMap<Integer,Integer> foodAgeList = new HashMap<>();
        for (int i = 0; i < game.getMap().getFoodCells().length; i++) {
            Food food = (Food)game.getMap().getFoodCells()[i].getItem();
            foodList.put(food.getId(),food);
            foodAgeList.put(food.getId(),food.getRemainingTurns());
        }
        HashMap<Integer,Trash> trashList = new HashMap<>();
        HashMap<Integer,Integer> trashAgeList = new HashMap<>();
        for (int i = 0; i < game.getMap().getTrashCells().length; i++) {
            Trash trash = (Trash)game.getMap().getTrashCells()[i].getItem();
            trashList.put(trash.getId(),trash);
            trashAgeList.put(trash.getId(),trash.getRemainingTurns());
        }
        HashMap<Integer,Slipper> slipperList = new HashMap<>();
        HashMap<Integer,Integer> slipperAgeList = new HashMap<>();
        for (int i = 0; i < game.getMap().getSlipperCells().length; i++) {
            Slipper slipperItem = (Slipper)game.getMap().getSlipperCells()[i].getSlipper();
            if(slipperItem!=null) {
                slipperList.put(slipperItem.getId(), slipperItem);
                slipperAgeList.put(slipperItem.getId(), slipperItem.getRemainingTurns());
            }
        }
        eater =new AttackGameState(game,atackerBeetle.getDirection(),atackerBeetle.getPosition().getX()
                ,atackerBeetle.getPosition().getY(),atackerBeetle.getPower(),foodList,foodAgeList,trashList,trashAgeList
                ,slipperList,slipperAgeList,null);
    }
    boolean existAnswer =false;
    List<AttackGameState> openList;
    HashMap<String,AttackGameState> seenState;
    public void bfsSearch(){
        if (atackerBeetle==null)
            return;
        openList = new ArrayList<>();
        seenState =new HashMap<>();
        openList.add(eater);
        while (openList.size()>0){
            AttackGameState state = openList.remove(0);

            if(isFinishGame(state)){

                producePath(state);
                break;
            }
            AttackGameState moveState = new AttackGameState(state.getGame(),state.getBeetleDir(),state.getBeetlePosX()
                    ,state.getBeetlePosY(),state.getBeetlePower(),state.getFoodList(),state.getFoodAgeList()
                    ,state.getTrashList(),state.getTrashAgeList(),state.getSlipperList(),state.getSlipperAgeList(),state);
            if(moveState.moveForward()){
                if (!seenState.containsKey(moveState.getKey())) {
                    openList.add(moveState);
                    seenState.put(moveState.getKey(),moveState);
                }
            }
            AttackGameState turnLeftState = new AttackGameState(state.getGame(),state.getBeetleDir(),state.getBeetlePosX()
                    ,state.getBeetlePosY(),state.getBeetlePower(),state.getFoodList(),state.getFoodAgeList()
                    ,state.getTrashList(),state.getTrashAgeList(),state.getSlipperList(),state.getSlipperAgeList(),state);
            turnLeftState.turnLeft();
            if (!seenState.containsKey(turnLeftState.getKey())) {
                openList.add(turnLeftState);
                seenState.put(turnLeftState.getKey(),turnLeftState);
            }

            AttackGameState turnRightState = new AttackGameState(state.getGame(),state.getBeetleDir(),state.getBeetlePosX()
                    ,state.getBeetlePosY(),state.getBeetlePower(),state.getFoodList(),state.getFoodAgeList()
                    ,state.getTrashList(),state.getTrashAgeList(),state.getSlipperList(),state.getSlipperAgeList(),state);
            turnRightState.turnRight();
            if (!seenState.containsKey(turnRightState.getKey())) {
                openList.add(turnRightState);
                seenState.put(turnRightState.getKey(),turnRightState);
            }


        }
    }
    List<AttackGameState> answerList;
    private void producePath(AttackGameState rootState){
        existAnswer =true;
        AttackGameState tempNode = rootState;
        answerList = new ArrayList<>();
        while (tempNode !=null){
            answerList.add(0,tempNode);
            tempNode = tempNode.getParent();
        }
    }

    public List<AttackGameState> getAnswerList() {
        return answerList;
    }

    public boolean isExistAnswer() {
        return existAnswer;
    }
    private boolean isFinishGame(AttackGameState state){
        for (Integer key:state.getFoodList().keySet()) {
            if(state.getBeetlePosX() == state.getFoodList().get(key).getPosition().getX()
                    && state.getBeetlePosY() ==state.getFoodList().get(key).getPosition().getY()
                    && state.getFoodAgeList().get(key)>0){
                return true;
            }
        }
        return false;
    }
}
