package client.freezAi.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohamad on 2/23/17.
 */
/*
* search for arrive to food
* */


public class AStarSearch {

    private static final String TAG = "AStarSearch: ";
    int MAx_HEURISTIC = 0;
    GameState rootState;
    List<GameState> openList;
    List<GameState> closeList;
    List<GameState> answerList;
    boolean existAnswer =false;
    public AStarSearch(GameState initState) {
        openList = new ArrayList<>();
        closeList = new ArrayList<>();
        answerList = new ArrayList<>();
        rootState = initState;
        this.MAx_HEURISTIC = 5*(rootState.getMapWidth() + rootState.getMapHeight());
        startSearch();
    }
    private void startSearch(){
        openList.add(rootState);
        GameState mainNode = null;
        while (openList.size()>0){
            mainNode = openList.remove(0);

//            if(mainNode==null)
//                System.out.println("AStarSearch: startSearch: main node is null");
//            System.out.println(TAG+" mainNode: xy=" + mainNode.getBeetlePosX()+","+mainNode.getBeetlePosY()+"   dir= "
//                    +mainNode.getBeetleDir()+" heuristic="+mainNode.getHeutristic() );
//            System.out.println(TAG+" mainNode: xy=" + mainNode.getBeetlePosX()+","+mainNode.getBeetlePosY()+"   dir= "+mainNode.getBeetleDir() );
            closeList.add(mainNode);
            if(mainNode.getHeutristic() == MAx_HEURISTIC ){
                continue;
            }
            else if (mainNode.getHeutristic() == 0) {
//                System.out.println(TAG+ "h=0 in node.pos="+mainNode.getBeetlePosX()+","+mainNode.getBeetlePosY());
                printAnswer(mainNode);
                break;
            }

            GameState moveNode = new GameState(mainNode.getGame(),mainNode.getBeetleDir()
                    ,mainNode.getBeetlePosX(),mainNode.getBeetlePosY(),mainNode.getBeetlePower()
                    ,mainNode.getFoodList(),mainNode.getFoodAgeList(),mainNode.getTrashList(),mainNode.getTrashAgeList()
                    ,mainNode.getSlipperList(),mainNode.getSlipperAgeList(),mainNode);
            if(moveNode.moveForward()) {
                if (!existInCloseList(moveNode)) {
                    moveNode.setF(moveNode.getCost() + moveNode.getHeutristic());
                    updateOpenList(moveNode);
                }
            }
            moveNode.printFood();
//            System.out.println(TAG+" moveNode: xy=" + moveNode.getBeetlePosX()+","+moveNode.getBeetlePosY()+"   dir= "
//                    +moveNode.getBeetleDir()+" heuristic="+moveNode.getHeutristic() );

            GameState turnLeftNode = new GameState(mainNode.getGame(),mainNode.getBeetleDir()
                    ,mainNode.getBeetlePosX(),mainNode.getBeetlePosY(),mainNode.getBeetlePower()
                    ,mainNode.getFoodList(),mainNode.getFoodAgeList(),mainNode.getTrashList(),mainNode.getTrashAgeList()
                    ,mainNode.getSlipperList(),mainNode.getSlipperAgeList(),mainNode);
            turnLeftNode.turnLeft();
            turnLeftNode.printFood();
            if(!existInCloseList(turnLeftNode)){
                turnLeftNode.setF(turnLeftNode.getCost()+turnLeftNode.getHeutristic());
                updateOpenList(turnLeftNode);
            }

//            System.out.println(TAG+" turnLeftNode: xy=" + turnLeftNode.getBeetlePosX()+","+turnLeftNode.getBeetlePosY()+"   dir= "
//                    +turnLeftNode.getBeetleDir()+" heuristic="+turnLeftNode.getHeutristic() );
            GameState turnRightNode = new GameState(mainNode.getGame(),mainNode.getBeetleDir()
                    ,mainNode.getBeetlePosX(),mainNode.getBeetlePosY(),mainNode.getBeetlePower()
                    ,mainNode.getFoodList(),mainNode.getFoodAgeList(),mainNode.getTrashList(),mainNode.getTrashAgeList()
                    ,mainNode.getSlipperList(),mainNode.getSlipperAgeList(),mainNode);
            turnRightNode.turnRight();
            turnRightNode.printFood();
            if(!existInCloseList(turnRightNode)){
                turnRightNode.setF(turnRightNode.getCost()+turnRightNode.getHeutristic());
                updateOpenList(turnRightNode);
            }

//            System.out.println(TAG+" turnRightNode: xy=" + turnRightNode.getBeetlePosX()+","+turnRightNode.getBeetlePosY()+"   dir= "
//                    +turnRightNode.getBeetleDir()+" heuristic="+turnRightNode.getHeutristic() );
//            System.out.println("--------------------------------------------------------------");
        }
    }

    private void printAnswer(GameState mainNode) {
//        System.out.println("AStarSearch.printAnswer: start");
        existAnswer =true;
        GameState tempNode = mainNode;
        while (tempNode !=null){
//            if(answerList.size()>0){
//                if(answerList.get(0).getBeetlePosX()!=tempNode.getBeetlePosX()
//                        || answerList.get(0).getBeetlePosY()!=tempNode.getBeetlePosY()){
//                    answerList.add(0,tempNode);
//                }
//            }else{
//                answerList.add(0,tempNode);
//            }
            answerList.add(0,tempNode);
            tempNode = tempNode.getParent();
        }
//        System.out.println("AStarSearch.printAnswer: answerList.size="+answerList.size());;

    }

    private void updateOpenList(GameState state){
        int index = 0;
        for (int i = 0; i < openList.size(); i++) {
            if(openList.get(i).getCost()+openList.get(i).getHeutristic() < state.getCost()+state.getHeutristic()){
                index+=1;
            }
            if(state.getKey().equals(openList.get(i).getKey())){
                if(openList.get(i).getCost() >state.getCost()){
                    openList.get(i).setCost(state.getCost());
                    openList.get(i).setParent(state);
                    return;
                }
            }
        }
        openList.add(index,state);
    }

    private boolean existInCloseList(GameState node) {
        for (int i = 0; i < closeList.size(); i++) {
            if(closeList.get(i).getKey().equals(node.getKey()))
                return true;
        }
        return false;
    }

    public List<GameState> getAnswerList() {
        return answerList;
    }

    public boolean isExistAnswer() {
        return existAnswer;
    }
}
