package client.freezAi.models;

import client.World;
import client.model.Beetle;
import client.model.Food;
import client.model.Slipper;
import client.model.Trash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mohamad on 3/8/17.
 */
public class Attack {

    Beetle atackerBeetle;
    Beetle preyBeetle;
    AttackGameState rootState;
    public Attack(World game,Beetle btl,Beetle preyBeetle) {
        this.atackerBeetle =btl;
        this.preyBeetle = preyBeetle;
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
        rootState =new AttackGameState(game,atackerBeetle.getDirection(),atackerBeetle.getPosition().getX()
                ,atackerBeetle.getPosition().getY(),atackerBeetle.getPower(),foodList,foodAgeList,trashList,trashAgeList
                ,slipperList,slipperAgeList,null);
    }
    boolean existAnswer =false;
    List<AttackGameState> openList;
    HashMap<String,AttackGameState> seenState;
    public void bfsSearch(){
        if (preyBeetle==null)
            return;
        if (atackerBeetle==null)
            return;
        seenState = new HashMap<>();
        openList = new ArrayList<>();
        openList.add(rootState);
        while (openList.size()>0){
            AttackGameState state = openList.remove(0);

            if(state.getBeetlePosX() == preyBeetle.getPosition().getX()
                    && state.getBeetlePosY() == preyBeetle.getPosition().getY()){

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
    public void printAnswer(){
        System.out.println("=========beetle id: "+atackerBeetle.getPosition().getX()+","+atackerBeetle.getPosition().getY());
        System.out.print("[");
        for (int i = 0; i < answerList.size(); i++) {
            System.out.print("("+answerList.get(i).getBeetlePosX() +","+answerList.get(i).getBeetlePosY()+","+answerList.get(i).getBeetleDir()+"),");
        }
        System.out.print("]\n");
    }
}
