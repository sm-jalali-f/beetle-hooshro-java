package client.freezAi.models;

import client.World;
import client.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mohamad on 2/25/17.
 */
public class BeetleSlipperBreaker {
    int mapHeight,mapWidth;
    List<Cell> SlipperCellList;
    Beetle btl;
    Slipper slp;
    World game;
    List<BfsGameState> openList;
    List<BfsGameState> answerList;
    HashMap<String,BfsGameState> seenState;
    BfsGameState rootState;
    boolean existAnswer =false;
    public BeetleSlipperBreaker(Beetle btl, World game, Slipper slipper) {
        this.game = game;
        this.btl = btl;
        this.slp =slipper;
        mapWidth = game.getMap().getWidth();
        mapHeight = game.getMap().getHeight();
        SlipperCellList = getCellOfSlipper(slipper.getPosition().getX(),slipper.getPosition().getY());
        seenState =new HashMap<>();
        openList = new ArrayList<>();
        answerList = new ArrayList<>();
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
        rootState = new BfsGameState(game,btl.getDirection()
                ,btl.getPosition().getX(),btl.getPosition().getY(),btl.getPower()
                ,foodList,foodAgeList,trashList,trashAgeList
                ,slipperList,slipperAgeList,null
                ,slp.getId(),slp.getRemainingTurns());

    }




    public void bestBfsSearch(){
        openList.add(rootState);
        while (openList.size()>0){
            BfsGameState mainNode = openList.remove(0);
            if(!isUnderSlipper(mainNode.getBeetlePosX(),mainNode.getBeetlePosY())){
                producePath(mainNode);
                return;
            }
            BfsGameState moveNode = new BfsGameState(mainNode.getGame(),mainNode.getBeetleDir()
                    ,mainNode.getBeetlePosX(),mainNode.getBeetlePosY(),mainNode.getBeetlePower()
                    ,mainNode.getFoodList(),mainNode.getFoodAgeList(),mainNode.getTrashList(),mainNode.getTrashAgeList()
                    ,mainNode.getSlipperList(),mainNode.getSlipperAgeList(),mainNode
                    ,mainNode.getSlipperId(),mainNode.getSlipperRemainTurn());
            if(moveNode.moveForward()) {
                if (moveNode.isHelpfulState()) {
                    if (!seenState.containsKey(moveNode.getKey())) {
                        openList.add(moveNode);
                        seenState.put(moveNode.getKey(),moveNode);
                    }
                }
            }
            BfsGameState turnLeftNode = new BfsGameState(mainNode.getGame(),mainNode.getBeetleDir()
                ,mainNode.getBeetlePosX(),mainNode.getBeetlePosY(),mainNode.getBeetlePower()
                ,mainNode.getFoodList(),mainNode.getFoodAgeList(),mainNode.getTrashList(),mainNode.getTrashAgeList()
                ,mainNode.getSlipperList(),mainNode.getSlipperAgeList(),mainNode
                ,mainNode.getSlipperId(),mainNode.getSlipperRemainTurn());
            turnLeftNode.turnLeft();
//            if()
            if (turnLeftNode.isHelpfulState()) {
                if (!seenState.containsKey(turnLeftNode.getKey())) {
                    openList.add(turnLeftNode);
                    seenState.put(turnLeftNode.getKey(),turnLeftNode);
                }
            }
            BfsGameState turnRightNode = new BfsGameState(mainNode.getGame(),mainNode.getBeetleDir()
                    ,mainNode.getBeetlePosX(),mainNode.getBeetlePosY(),mainNode.getBeetlePower()
                    ,mainNode.getFoodList(),mainNode.getFoodAgeList(),mainNode.getTrashList(),mainNode.getTrashAgeList()
                    ,mainNode.getSlipperList(),mainNode.getSlipperAgeList(),mainNode
                    ,mainNode.getSlipperId(),mainNode.getSlipperRemainTurn());
            turnRightNode.turnLeft();
            if (turnRightNode.isHelpfulState()) {
                if (!seenState.containsKey(turnRightNode.getKey())) {
                    openList.add(turnRightNode);
                    seenState.put(turnRightNode.getKey(),turnRightNode);
                }
            }

        }
    }

    private void producePath(BfsGameState rootState){
        existAnswer =true;
        BfsGameState tempNode = rootState;
        answerList = new ArrayList<>();
        while (tempNode !=null){
            answerList.add(0,tempNode);
            tempNode = tempNode.getParent();
        }
    }


    private boolean isUnderSlipper(int x, int y){
        for (int i = 0; i < SlipperCellList.size(); i++) {
            if(SlipperCellList.get(i).getX() ==x && SlipperCellList.get(i).getY() == y){
                return true;
            }
        }
        return false;

    }
    private List<Cell> getCellOfSlipper(int slipperX, int slipperY){
        Cell leftTopCell = new Cell(slipperX-1,slipperY-1);
        if(leftTopCell.getX()==-1)
            leftTopCell.setX(mapHeight-1);
        if(leftTopCell.getY()==-1)
            leftTopCell.setY(mapWidth-1);
        Cell topCell = new Cell(slipperX-1,slipperY);
        if(topCell.getX()==-1)
            topCell.setX(mapHeight-1);
        Cell rightTopCell = new Cell(slipperX-1,slipperY+1);
        if(rightTopCell.getX()==-1)
            rightTopCell.setX(mapHeight-1);
        if(rightTopCell.getY()==mapWidth)
            rightTopCell.setY(0);
        Cell rightCell = new Cell(slipperX,slipperY+1);
        if(rightCell.getY()==mapWidth)
            rightCell.setY(0);
        Cell rightBottomCell = new Cell(slipperX+1,slipperY+1);
        if(rightBottomCell.getX()==mapHeight)
            rightBottomCell.setX(0);
        if(rightBottomCell.getY()==mapWidth)
            rightBottomCell.setY(0);
        Cell bottomCell = new Cell(slipperX+1,slipperY);
        if(bottomCell.getX()==mapHeight)
            bottomCell.setX(0);
        Cell leftBottomCell = new Cell(slipperX+1,slipperY-1);
        if(leftBottomCell.getX()==mapHeight)
            leftBottomCell.setX(0);
        if(leftBottomCell.getY()==-1)
            leftBottomCell.setY(mapWidth-1);
        Cell leftCell = new Cell(slipperX,slipperY-1);
        if(leftCell.getY()==-1)
            leftCell.setY(mapWidth-1);
        List <Cell>CellList = new ArrayList<>();
        CellList.add(leftTopCell);
        CellList.add(topCell);
        CellList.add(rightTopCell);
        CellList.add(leftCell);
        CellList.add(new Cell(slipperX,slipperY));
        CellList.add(rightCell);
        CellList.add(leftBottomCell);
        CellList.add(bottomCell);
        CellList.add(rightBottomCell);
        return CellList;
    }

    public boolean isExistAnswer() {
        return existAnswer;
    }


    public List<BfsGameState> getPath() {
        return answerList;
    }
}
