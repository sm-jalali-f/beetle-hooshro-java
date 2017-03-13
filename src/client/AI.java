package client;

import client.freezAi.Utility;
import client.freezAi.models.*;
import client.model.*;

import java.util.*;

/**
 * Created by mohamad on 2/23/17.
 */
public class AI {

    int teamId;
    HashMap<Integer,BeetleStrategy> myBeetleStrategyHashMap;
    HashMap<Integer,List<AttackGameState>> myBeetleFoodPath;
    HashMap<Integer,List<AttackGameState>> myBeetleAttackPath;
    HashMap<Integer,List<BfsGameState>> myBeetleSlipperPath;

    HashMap<Integer,Move> moveLowUpdateHashMap; // store update function
    HashMap<Integer,Move> moveHighUpdateHashMap; // store update function

    HashMap<Integer,Move> myBeetleMove; //move for each beetle

    HashMap<Integer,List<Beetle>> lowBeetleStates =new HashMap<>(); //key is BeetleState
    HashMap<Integer,List<Beetle>> highBeetleStates =new HashMap<>(); //key is BeetleState , which high beetle in this state

    static List<Cell>dangerCell =new ArrayList<>();
    int mapWidth;
    int mapHeight;
    World currentGame;
    public AI() {
        myBeetleFoodPath = new HashMap<>();
        myBeetleAttackPath = new HashMap<>();
        myBeetleSlipperPath = new HashMap<>();
        moveLowUpdateHashMap = new HashMap<>();
        moveHighUpdateHashMap = new HashMap<>();
        myBeetleStrategyHashMap =new HashMap<>();

    }

    
    public synchronized void doTurn(World game){
        currentGame =game;

        System.out.println("==========================  " +currentGame.getCurrentTurn()+"  =====================");
        System.out.println("==========================  pts: " +currentGame.getMyScore()+"  =====================");
        if(currentGame.getCurrentTurn()==0){
            initializeGame();
        }
        dangerCell = new ArrayList<>();
        for (int i = 0; i < game.getMap().getOppCells().length; i++) {
            dangerCell.add(game.getMap().getOppCells()[i]);
            dangerCell.add(Utility.getLeft(game.getMap().getOppCells()[i],mapWidth));
            dangerCell.add(Utility.getRight(game.getMap().getOppCells()[i],mapWidth));
            dangerCell.add(Utility.getTop(game.getMap().getOppCells()[i],mapWidth));
            dangerCell.add(Utility.getBottom(game.getMap().getOppCells()[i],mapWidth));
        }

        myBeetleMove =new HashMap<>();
        lowBeetleStates = new HashMap<>();
        highBeetleStates = new HashMap<>();
        Beetle attacker = getBestAttackerBeetle();

        for (int i = 0; i < currentGame.getMap().getMyCells().length; i++) {
            Beetle btl = (Beetle)currentGame.getMap().getMyCells()[i].getBeetle();
            myBeetleStrategyHashMap.put(btl.getId(),BeetleStrategy.Nothing);
            updateBeetleStatesHashMap(calculateBeetleState(btl),btl);
            Slipper fuckingSlipper = getSlipperOnBeetle(btl);
            if(fuckingSlipper!=null){
//                System.out.println("slipper in my beetle");
                BeetleSlipperBreaker slipperBreaker = new BeetleSlipperBreaker(btl,currentGame,fuckingSlipper);
                slipperBreaker.bestBfsSearch();
                if(slipperBreaker.isExistAnswer()) {
//                    System.out.println("slipper in my beetle and find answere");
                    myBeetleStrategyHashMap.put(btl.getId(), BeetleStrategy.SlipperBreak);
                    myBeetleSlipperPath.put(btl.getId(),slipperBreaker.getPath());
//                    System.out.print("slipperPath: ");
//                    for (int j = 0; j < slipperBreaker.getPath().size(); j++) {
//                        System.out.print("("+slipperBreaker.getPath().get(j).getBeetlePosX()
//                                +","+slipperBreaker.getPath().get(j).getBeetlePosY()
//                                +","+slipperBreaker.getPath().get(j).getBeetleDir()+"),");
//                    }
//                    System.out.println();

                }else{
//                    System.out.println("slipper in my beetle don't find answere");
                }
            }else{
                if(attacker.getId()==btl.getId()){
                    Beetle preyBetle=getWeekestEnemy(attacker);
                    if(preyBetle!=null) {
                        Attack attackStrategy = new Attack(currentGame, attacker, preyBetle);
                        attackStrategy.bfsSearch();
                        if (attackStrategy.isExistAnswer()) {
                            attackStrategy.printAnswer();
//                        System.out.println("Attack");
                            myBeetleStrategyHashMap.put(btl.getId(), BeetleStrategy.EnemyFollow);
                            List<AttackGameState> states = attackStrategy.getAnswerList();
                            if (states != null) {
                                if (states.size() > 0) {
                                    myBeetleAttackPath.put(btl.getId(), states);
                                }
                            }
                        } else {
                            findPathToFood(btl);
                        }
                    }else{
                        findPathToFood(btl);
                    }
                }else {

                    findPathToFood(btl);
                }

            }

            //if new beetle created in hashmap not exist key and strategy
            if(!myBeetleStrategyHashMap.containsKey(btl.getId())){
                myBeetleStrategyHashMap.put(btl.getId(),BeetleStrategy.Nothing);
            }
//            Move btlMove = computeMove(btl);
            myBeetleStrategyHashMap.put(btl.getId(),BeetleStrategy.Nothing);
            Move btlMove = Move.stepForward;
//            if(myBeetleStrategyHashMap.get(btl.getId())!=BeetleStrategy.EnemyFollow) {
//                if (btlMove == Move.stepForward && myBeetleStrategyHashMap.get(btl.getId()) != BeetleStrategy.SlipperBreak) {
                    Cell nextCell = getPositionOfMoveForward(btl.getPosition().getX(), btl.getPosition().getY(), btl.getDirection());
                    if (isExistSlipper(nextCell.getX(), nextCell.getY()) && !isExistSlipper(btl.getPosition().getX()
                            ,btl.getPosition().getY())) {
                        btlMove = getBestMoveByBreakEnemy(currentGame.getMap().getMyCells()[i], btl.getDirection()); //TODO whyyyyyyyy choose better move
                        myBeetleStrategyHashMap.put(btl.getId(), BeetleStrategy.SlipperBreak);
//                    }
//                    else if(isExistTrash(nextCell.getX(), nextCell.getY()) && isExistSlipper(btl.getPosition().getX()
//                            ,btl.getPosition().getY())){
//                        btlMove = getBestMoveByBreakEnemy(currentGame.getMap().getMyCells()[i], btl.getDirection()); //TODO whyyyyyyyy choose better move
//                        myBeetleStrategyHashMap.put(btl.getId(), BeetleStrategy.TrashBreak);
//
                    } else if (isExistTrash(nextCell.getX(), nextCell.getY())) {
                        btlMove = getBestMoveByBreakEnemy(currentGame.getMap().getMyCells()[i], btl.getDirection()); //TODO whyyyyyyyy choose better move
                        myBeetleStrategyHashMap.put(btl.getId(), BeetleStrategy.TrashBreak);
                    }  else {

                        int ownPower = btl.getPower();
                        Cell cell = game.getMap().getCell(nextCell.getX(), nextCell.getY());
                        if (cell.getBeetle() != null) {
                            if (((Beetle) cell.getBeetle()).getTeam() == teamId) {
                                ownPower += ((Beetle) cell.getBeetle()).getPower();
                            }
                        }
                        if (ownPower < getPowerOfEnemyAroundPosition(nextCell.getX(), nextCell.getY())) { // if power of opponent grater than my beetle
                            btlMove = getBestMoveByBreakEnemy(currentGame.getMap().getMyCells()[i], btl.getDirection()); //TODO whyyyyyyyy choose better move
                            myBeetleStrategyHashMap.put(btl.getId(), BeetleStrategy.EnemyBreak);
                        }
                    }
//                }
//            }
            myBeetleMove.put(btl.getId(),btlMove);
        }
//        for (Integer btlId:myBeetleMove.keySet()){
//            System.out.println("beetle.id: "+btlId+"  strategy:"+myBeetleStrategyHashMap.get(btlId)+"  move:"+myBeetleMove.get(btlId));
//        }
        // set action for low beetles:
        manageLowBeetleMovement();
        // set action for high beetles:
        manageHighBeetleMovement();
//        for (int i = 0; i < game.getMap().getMyCells().length; i++) {
//            Beetle btl = (Beetle)game.getMap().getMyCells()[i].getBeetle();
//            if(btl.has_winge()) {
//
//                Move myBtlMove = myBeetleMove.get(btl.getId());
//                BeetleState state = calculateBeetleState(btl);
//                Move changeUpdate = null;
//                if (btl.getBeetleType() == BeetleType.LOW) {
//                    changeUpdate = moveLowUpdateHashMap.get(state.getValue());
//                } else {
//                    changeUpdate = moveHighUpdateHashMap.get(state.getValue());
//                }
//                if(changeUpdate!=null) {
//                    if (myBtlMove != changeUpdate) {
//                        System.out.println("determinestice move = " + myBtlMove);
//                        game.deterministicMove(btl, myBtlMove);
//                    }
//                }
//            }
//
//        }

        int normalLowBtl=0;
        int normalHighBtl=0;
        int wingLowBtl=0;
        int wingHighBtl=0;
        System.out.println("-=======================================================");
        for (int i = 0; i < currentGame.getMap().getMyCells().length; i++) {
            Beetle btl = (Beetle)currentGame.getMap().getMyCells()[i].getBeetle();
            if(btl.has_winge() && btl.getBeetleType()==BeetleType.LOW){
                wingLowBtl +=1;
            }else if(btl.has_winge() && btl.getBeetleType()==BeetleType.HIGH){
                wingHighBtl +=1;
            }else if(!btl.has_winge() && btl.getBeetleType()==BeetleType.LOW){
                normalLowBtl +=1;
            }else if(!btl.has_winge() && btl.getBeetleType()==BeetleType.HIGH){
                normalHighBtl+=1;
            }
        }
//        System.out.println("wing.low="+wingLowBtl);
//        System.out.println("wing.high="+wingHighBtl);
//        System.out.println("normal.low="+normalLowBtl);
//        System.out.println("normal.high="+normalHighBtl);

        if(wingHighBtl>wingLowBtl){
            int diff = wingHighBtl - wingLowBtl;
            diff =diff/2;
//            System.out.println("diff wingHigh-wingLow= " + diff);
            for (int i = 0; i < currentGame.getMap().getMyCells().length; i++) {
                if (diff == 0)
                    break;
                Beetle btl = (Beetle) currentGame.getMap().getMyCells()[i].getBeetle();
                if (btl.getBeetleType() == BeetleType.HIGH && btl.has_winge()) {
                    currentGame.changeType(btl, BeetleType.LOW);
                    diff -= 1;

                }
            }

        }else if (wingHighBtl<wingLowBtl){
            int diff = wingLowBtl - wingHighBtl;
            diff =diff/2;
//            System.out.println("diff wingLow-wingHigh= " + diff);
            for (int i = 0; i < currentGame.getMap().getMyCells().length; i++) {
                if (diff == 0)
                    break;
                Beetle btl = (Beetle) currentGame.getMap().getMyCells()[i].getBeetle();
                if (btl.getBeetleType() == BeetleType.LOW && btl.has_winge()) {
                    currentGame.changeType(btl, BeetleType.HIGH);
                    diff -= 1;

                }
            }
        }
        if(normalHighBtl>normalLowBtl){
            int diff = normalHighBtl - normalLowBtl;
            diff =diff/2;
            for (int i = 0; i < currentGame.getMap().getMyCells().length; i++) {
                if (diff == 0)
                    break;
                Beetle btl = (Beetle) currentGame.getMap().getMyCells()[i].getBeetle();
                if (btl.getBeetleType() == BeetleType.HIGH && !btl.has_winge()) {
                    currentGame.changeType(btl, BeetleType.LOW);
                    diff -= 1;

                }
            }

        }else if (normalHighBtl<normalLowBtl){
            int diff = normalLowBtl - normalHighBtl;
            diff =diff/2;
            for (int i = 0; i < currentGame.getMap().getMyCells().length; i++) {
                if (diff == 0)
                    break;
                Beetle btl = (Beetle) currentGame.getMap().getMyCells()[i].getBeetle();
                if (btl.getBeetleType() == BeetleType.LOW && !btl.has_winge()) {
                    currentGame.changeType(btl, BeetleType.HIGH);
                    diff -= 1;

                }
            }
        }
    }
    public Move getBestMoveByBreakEnemy(Cell cell,Direction currentDir){
        if(currentDir ==Direction.Up){
            Move move =Move.stepForward;
            int max = 0;

            int temp = getMaxDistanceWithDangerCell(Utility.getLeft(cell,mapWidth));
            if(temp>max) {
                max = temp;
                move=Move.turnLeft;
            }
            temp = getMaxDistanceWithDangerCell(Utility.getRight(cell,mapWidth));
            if(temp>max) {
                max = temp;
                move = Move.turnRight;
            }
            return move;
        }else if(currentDir ==Direction.Down) {
            Move move=Move.stepForward;
            int max = 0;

            int temp = getMaxDistanceWithDangerCell(Utility.getLeft(cell,mapWidth));
            if(temp>max) {
                max = temp;
                move=Move.turnRight;
            }
            temp = getMaxDistanceWithDangerCell(Utility.getRight(cell,mapWidth));
            if(temp>max) {
                max = temp;
                move = Move.turnLeft;
            }
            return move;
        }
        else if(currentDir ==Direction.Left) {
            Move move=Move.stepForward;
            int max = 0;
            int temp = getMaxDistanceWithDangerCell(Utility.getBottom(cell,mapHeight));
            if(temp>max){
                max = temp;
                move=Move.turnLeft;
            }

            temp = getMaxDistanceWithDangerCell(Utility.getTop(cell,mapHeight));
            if(temp>max) {
                max = temp;
                move = Move.turnRight;
            }
            return move;
        }else if(currentDir ==Direction.Right) {
            Move move=Move.stepForward;
            int max = 0;
            int temp = getMaxDistanceWithDangerCell(Utility.getBottom(cell,mapHeight));
            if(temp>max){
                max = temp;
                move=Move.turnRight;
            }
            temp = getMaxDistanceWithDangerCell(Utility.getTop(cell,mapHeight));
            if(temp>max) {
                max = temp;
                move = Move.turnLeft;
            }
            return move;
        }
        return  Move.stepForward;
    }
    private int calculateManhatanDistance(Cell src,Cell des){
        int bestDistance = 5*(mapHeight+mapWidth);
//        System.out.println("GameState.calculate_manhatan_distance_with_food: foodList.size: "+foodList.size());
        int diffX = Math.abs(src.getX() - des.getX());
        diffX = Math.min(diffX,mapHeight-diffX);
        int diffY = Math.abs(src.getY() - des.getY());
        diffY = Math.min(diffY,mapWidth-diffY);
        int distance = diffX+diffY;
        if(distance<bestDistance)
                bestDistance = distance;

        return bestDistance;
    }

    public int getMaxDistanceWithDangerCell(Cell cell){
        int min=mapWidth+mapHeight+100;
        for (int i = 0; i < dangerCell.size(); i++) {
            int temp =calculateManhatanDistance(cell,dangerCell.get(i));
            if(temp<min){
                min = temp;
            }
        }
        return min;
    }



    private int getPowerOfEnemyAroundPosition(int x, int y) {
        int power =0;
        Cell topCell =getPositionOfMoveForward(x,y,Direction.Up);
        Cell leftCell =getPositionOfMoveForward(x,y,Direction.Left);
        Cell rightCell =getPositionOfMoveForward(x,y,Direction.Right);
        Cell bottomCell =getPositionOfMoveForward(x,y,Direction.Down);
        for (int i = 0; i < currentGame.getMap().getOppCells().length; i++) {
            if(((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPosition().getX() ==x &&
                    ((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPosition().getY() ==y){
                power+=((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPower();
            }
            else if(((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPosition().getX() ==topCell.getX()
                    &&((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPosition().getY() ==topCell.getY()
                    && ((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getDirection() ==Direction.Down){
                power+=((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPower();
            }else if(((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPosition().getX() ==leftCell.getX()
                    &&((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPosition().getY() ==leftCell.getY()
                    && ((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getDirection() ==Direction.Right){
                power+=((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPower();
            }else if(((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPosition().getX() ==rightCell.getX()
                    &&((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPosition().getY() ==rightCell.getY()
                    && ((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getDirection() ==Direction.Left){
                power+=((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPower();
            }else if(((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPosition().getX() ==bottomCell.getX()
                    &&((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPosition().getY() ==bottomCell.getY()
                    && ((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getDirection() ==Direction.Up){
                power+=((Beetle)currentGame.getMap().getOppCells()[i].getBeetle()).getPower();
            }
        }
        return power;

    }


    private boolean isExistTrash(int x, int y) {
        for (int i = 0; i < currentGame.getMap().getTrashCells().length; i++) {
            Trash trash =(Trash)currentGame.getMap().getTrashCells()[i].getItem();
            if(trash.getPosition().getX() ==x && trash.getPosition().getY() ==y)
                return true;
        }
        return false;
    }
    private boolean isExistSlipper(int x, int y) {
        for (int i = 0; i < currentGame.getMap().getSlipperCells().length; i++) {
            Slipper slp = (Slipper)currentGame.getMap().getSlipperCells()[i].getSlipper();
            if(Utility.isUnderSlipper(new Cell(x,y),new Cell(slp.getPosition().getX(),slp.getPosition().getY())
                    ,mapHeight,mapWidth))
                return true;
        }
        return false;
    }

    private Cell getPositionOfMoveForward(int x,int y,Direction dir){
        int resX,resY;
        switch (dir){
            case Down:
                resX =x+1;
                resY =y;
                break;
            case Right:
                resX =x;
                resY =y+1;
                break;
            case Left:
                resX =x;
                resY =y-1;
                break;
            case Up:
                resX =x-1;
                resY =y;
                break;
            default:
                resX =x;
                resY =y;

        }
        if(resX==-1){
            resX =mapHeight-1;
        }else if(resX == mapHeight){
            resX=0;
        }
        if(resY==-1){
            resY = mapWidth-1;
        }else if(resY == mapWidth){
            resY= 0;
        }
        return new Cell(resX,resY);
    }
    private void manageHighBeetleMovement(){
        int maxBeetlePower=0;
        maxBeetlePower =getMaxPower();
        for (Integer key:highBeetleStates.keySet()){
            List<Beetle> sameBeetleState =highBeetleStates.get(key);
//            ArrayList<Integer> sameBeetleStateWorth =new ArrayList<>();
            HashMap<Integer,Integer> sameBeetleStateWorth =new HashMap<>();//key is beetle id and value is worth
            for (int i = 0; i < sameBeetleState.size(); i++) {
                sameBeetleStateWorth.put(sameBeetleState.get(i).getId(),getBeetleWorth(sameBeetleState.get(i),maxBeetlePower));
            }
            LinkedHashMap<Integer,Integer> sortedWorth= sortHashMapByValues(sameBeetleStateWorth);
            int id =-1;
            for (Integer beetleId:sortedWorth.keySet()){
                id = beetleId;
            }
            if (id==-1)
                return;
            changeUpdateFunction(BeetleState.fromValue(key),BeetleType.HIGH,myBeetleMove.get(id));
        }
    }
    private void manageLowBeetleMovement(){
        int maxBeetlePower=0;
        maxBeetlePower =getMaxPower();
        for (Integer key:lowBeetleStates.keySet()){
            List<Beetle> sameBeetleState =lowBeetleStates.get(key);
            HashMap<Integer,Integer> sameBeetleStateWorth =new HashMap<>();//key is beetle id and value is worth
            for (int i = 0; i < sameBeetleState.size(); i++) {
                sameBeetleStateWorth.put(sameBeetleState.get(i).getId(),getBeetleWorth(sameBeetleState.get(i),maxBeetlePower));
            }
            LinkedHashMap<Integer,Integer> sortedWorth= sortHashMapByValues(sameBeetleStateWorth);

            int id =-1;
            for (Integer beetleId:sortedWorth.keySet()){
                id = beetleId;
            }
            if (id==-1)
                return;
            changeUpdateFunction(BeetleState.fromValue(key),BeetleType.LOW,myBeetleMove.get(id));

        }
    }
    static public LinkedHashMap<Integer, Integer> sortHashMapByValues(HashMap<Integer, Integer> passedMap) {
        List<Integer> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<Integer, Integer> sortedMap =
                new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Integer val = valueIt.next();
            Iterator<Integer> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Integer key = keyIt.next();
                Integer comp1 = passedMap.get(key);
                Integer comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
//        System.out.print("first hashmap: [ " );
//        for (Integer key:passedMap.keySet()){
//            System.out.print("("+key+","+passedMap.get(key)+") ,");
//        }
//        System.out.print("]\n" );
//        System.out.print("sorted hashmap: [ " );
//        for (Integer key:sortedMap.keySet()){
//            System.out.print("("+key+","+sortedMap.get(key)+") ,");
//        }
//        System.out.print("]\n" );

        return sortedMap;
    }
    private void changeUpdateFunction(BeetleState beetleState,BeetleType type, Move move) {
        if(type==BeetleType.LOW){
            if(moveLowUpdateHashMap.containsKey(beetleState)){
                if(moveLowUpdateHashMap.get(beetleState)!=move) {
                    currentGame.changeStrategy(type, BeetleState.getRight(beetleState.getValue())
                            , BeetleState.getFront(beetleState.getValue())
                            , BeetleState.getLeft(beetleState.getValue()), move);
//                    System.out.println("changeUpdate: type:"+type+ " , right:"+BeetleState.getRight(beetleState.getValue())
//                            +" , front:"+ BeetleState.getFront(beetleState.getValue())
//                            +" , left:"+BeetleState.getLeft(beetleState.getValue())+" , move="+move);
                    moveLowUpdateHashMap.put(beetleState.getValue(),move);
                }
            }else{
                currentGame.changeStrategy(type, BeetleState.getRight(beetleState.getValue())
                        , BeetleState.getFront(beetleState.getValue())
                        , BeetleState.getLeft(beetleState.getValue()), move);
//                System.out.println("changeUpdate: type:"+type+ " , right:"+BeetleState.getRight(beetleState.getValue())
//                        +" , front:"+ BeetleState.getFront(beetleState.getValue())
//                        +" , left:"+BeetleState.getLeft(beetleState.getValue())+" , move="+move);
                moveLowUpdateHashMap.put(beetleState.getValue(),move);
            }
        }else{
            if(moveHighUpdateHashMap.containsKey(beetleState)){
                if(moveHighUpdateHashMap.get(beetleState)!=move) {
                    currentGame.changeStrategy(type, BeetleState.getRight(beetleState.getValue())
                            , BeetleState.getFront(beetleState.getValue())
                            , BeetleState.getLeft(beetleState.getValue()), move);
                    moveHighUpdateHashMap.put(beetleState.getValue(),move);
//                    System.out.println("changeUpdate: type:"+type+ " , right:"+BeetleState.getRight(beetleState.getValue())
//                            +" , front:"+ BeetleState.getFront(beetleState.getValue())
//                            +" , left:"+BeetleState.getLeft(beetleState.getValue())+" , move="+move);
                }
            }else {
                currentGame.changeStrategy(type, BeetleState.getRight(beetleState.getValue())
                        , BeetleState.getFront(beetleState.getValue())
                        , BeetleState.getLeft(beetleState.getValue()), move);
                moveHighUpdateHashMap.put(beetleState.getValue(), move);
//                System.out.println("changeUpdate: type:"+type+ " , right:"+BeetleState.getRight(beetleState.getValue())
//                        +" , front:"+ BeetleState.getFront(beetleState.getValue())
//                        +" , left:"+BeetleState.getLeft(beetleState.getValue())+" , move="+move);
            }
        }
    }

    private Move computeMove(Beetle btl) {
        BeetleStrategy strategy = myBeetleStrategyHashMap.get(btl.getId());
        if(strategy == BeetleStrategy.SlipperBreak){
            List<BfsGameState> list =myBeetleSlipperPath.get(btl.getId());
            for (int i = 0; i < list.size()-1; i++) {
                if(list.get(i).getBeetlePosY()==btl.getPosition().getY()
                        && list.get(i).getBeetlePosX()==btl.getPosition().getX()
                        && list.get(i).getBeetleDir()==btl.getDirection()){
                    return determineNextMove(list.get(i),list.get(i+1));
                }

            }
        }else if(strategy==BeetleStrategy.EatFood){
            List<AttackGameState> list =myBeetleFoodPath.get(btl.getId());
            for (int i = 0; i < list.size()-1; i++) {
                if(list.get(i).getBeetlePosY()==btl.getPosition().getY()
                        && list.get(i).getBeetlePosX()==btl.getPosition().getX()
                        && list.get(i).getBeetleDir()==btl.getDirection()){
                    return determineNextMove(list.get(i),list.get(i+1));
                }

            }
        }else if(strategy==BeetleStrategy.EnemyFollow){
            List<AttackGameState> list =myBeetleAttackPath.get(btl.getId());
            for (int i = 0; i < list.size()-1; i++) {
                if(list.get(i).getBeetlePosY()==btl.getPosition().getY()
                        && list.get(i).getBeetlePosX()==btl.getPosition().getX()
                        && list.get(i).getBeetleDir()==btl.getDirection()){
                    return determineNextMove(list.get(i),list.get(i+1));
                }

            }
        }
        return Move.stepForward;

    }

    private Move determineNextMove(AttackGameState currentState, AttackGameState nextState) {
        if(currentState.getBeetlePosX() == nextState.getBeetlePosX()
                && currentState.getBeetlePosY() == nextState.getBeetlePosY()) { //yani charkhesh lazeme
            switch (nextState.getBeetleDir()){
                case Up:
                    switch (currentState.getBeetleDir()){
                        case Left:
                            return Move.turnRight;
                        case Right:
                            return Move.turnLeft;
                        case Down:
                            return Move.turnRight;
                    }
                    break;
                case Left:
                    switch (currentState.getBeetleDir()){
                        case Up:
                            return Move.turnLeft;
                        case Right:
                            return Move.turnRight;
                        case Down:
                            return Move.turnRight;
                    }
                    break;
                case Right:
                    switch (currentState.getBeetleDir()){
                        case Up:
                            return Move.turnRight;
                        case Left:
                            return Move.turnRight;
                        case Down:
                            return Move.turnLeft;

                    }
                    break;
                case Down:
                    switch (currentState.getBeetleDir()){
                        case Up:
                            return Move.turnRight;
                        case Left:
                            return Move.turnLeft;
                        case Right:
                            return Move.turnRight;
                    }
                    break;
            }

        }

        return Move.stepForward;
    }

    private  Slipper getSlipperOnBeetle(Beetle btl){
        for (int i = 0; i < currentGame.getMap().getSlipperCells().length; i++) {
            Slipper slp = (Slipper)currentGame.getMap().getSlipperCells()[i].getSlipper();
            if(Utility.isUnderSlipper(new Cell(btl.getPosition().getX(),btl.getPosition().getY())
                    ,new Cell(slp.getPosition().getX(),slp.getPosition().getY()),mapHeight,mapWidth)){
                return slp;
            }
        }
        return null;
    }

    private void updateBeetleStatesHashMap(BeetleState state, Beetle btl) {
        if(btl.getBeetleType() ==BeetleType.LOW){

            if(lowBeetleStates.containsKey(state.getValue())){
                lowBeetleStates.get(state.getValue()).add(btl);
            }else{
                List<Beetle> temp = new ArrayList<>();
                temp.add(btl);
                lowBeetleStates.put(state.getValue(),temp);
            }
        }else{
            if(highBeetleStates.containsKey(state.getValue())){
                highBeetleStates.get(state.getValue()).add(btl);
            }else{
                List<Beetle> temp = new ArrayList<>();
                temp.add(btl);
                highBeetleStates.put(state.getValue(),temp);
            }
        }
    }

    private Move determineNextMove(GameState currentState, GameState nextState) {
        if(currentState.getBeetlePosX() == nextState.getBeetlePosX()
                && currentState.getBeetlePosY() == nextState.getBeetlePosY()) { //yani charkhesh lazeme
            switch (nextState.getBeetleDir()){
                case Up:
                    switch (currentState.getBeetleDir()){
                        case Left:
                            return Move.turnRight;
                        case Right:
                            return Move.turnLeft;
                        case Down:
                            return Move.turnRight;
                    }
                    break;
                case Left:
                    switch (currentState.getBeetleDir()){
                        case Up:
                            return Move.turnLeft;
                        case Right:
                            return Move.turnRight;
                        case Down:
                            return Move.turnRight;
                    }
                    break;
                case Right:
                    switch (currentState.getBeetleDir()){
                        case Up:
                            return Move.turnRight;
                        case Left:
                            return Move.turnRight;
                        case Down:
                            return Move.turnLeft;

                    }
                    break;
                case Down:
                    switch (currentState.getBeetleDir()){
                        case Up:
                            return Move.turnRight;
                        case Left:
                            return Move.turnLeft;
                        case Right:
                            return Move.turnRight;
                    }
                    break;
            }

        }

        return Move.stepForward;
    }
    private Move determineNextMove(BfsGameState currentState, BfsGameState nextState) {
        if(currentState.getBeetlePosX() == nextState.getBeetlePosX()
                && currentState.getBeetlePosY() == nextState.getBeetlePosY()) { //yani charkhesh lazeme
            switch (nextState.getBeetleDir()){
                case Up:
                    switch (currentState.getBeetleDir()){
                        case Left:
                            return Move.turnRight;
                        case Right:
                            return Move.turnLeft;
                        case Down:
                            return Move.turnRight;
                    }
                    break;
                case Left:
                    switch (currentState.getBeetleDir()){
                        case Up:
                            return Move.turnLeft;
                        case Right:
                            return Move.turnRight;
                        case Down:
                            return Move.turnRight;
                    }
                    break;
                case Right:
                    switch (currentState.getBeetleDir()){
                        case Up:
                            return Move.turnRight;
                        case Left:
                            return Move.turnRight;
                        case Down:
                            return Move.turnLeft;

                    }
                    break;
                case Down:
                    switch (currentState.getBeetleDir()){
                        case Up:
                            return Move.turnRight;
                        case Left:
                            return Move.turnLeft;
                        case Right:
                            return Move.turnRight;
                    }
                    break;
            }

        }

        return Move.stepForward;
    }

    private void initializeGame(){
        this.mapHeight =currentGame.getMap().getHeight();
        this.mapWidth =currentGame.getMap().getWidth();
        this.teamId = currentGame.getTeamID();
        this.myBeetleStrategyHashMap = new HashMap<>();
        for (int i = 0; i < currentGame.getMap().getMyCells().length; i++) {
            myBeetleStrategyHashMap.put(currentGame.getMap().getMyCells()[i].getBeetle().getId(),BeetleStrategy.Nothing);
        }

    }
    private void findPathToFood(Beetle btl){
//        System.out.println("AI2.findPathToFood: start for btl.id: "+btl.getId());
        HashMap<Integer,Food> foodList = new HashMap<>();
        HashMap<Integer,Integer> foodAgeList = new HashMap<>();
        for (int i = 0; i < currentGame.getMap().getFoodCells().length; i++) {
            Food food = (Food)currentGame.getMap().getFoodCells()[i].getItem();
            foodList.put(food.getId(),food);
            foodAgeList.put(food.getId(),food.getRemainingTurns());
        }
        HashMap<Integer,Trash> trashList = new HashMap<>();
        HashMap<Integer,Integer> trashAgeList = new HashMap<>();
        for (int i = 0; i < currentGame.getMap().getTrashCells().length; i++) {
            Trash trash = (Trash)currentGame.getMap().getTrashCells()[i].getItem();
            trashList.put(trash.getId(),trash);
            trashAgeList.put(trash.getId(),trash.getRemainingTurns());
        }
        HashMap<Integer,Slipper> slipperList = new HashMap<>();
        HashMap<Integer,Integer> slipperAgeList = new HashMap<>();
        for (int i = 0; i < currentGame.getMap().getSlipperCells().length; i++) {
            if(currentGame.getMap().getSlipperCells()[i].getItem()!=null) {
                Slipper slipper = (Slipper) currentGame.getMap().getSlipperCells()[i].getSlipper();
                slipperList.put(slipper.getId(), slipper);
                slipperAgeList.put(slipper.getId(), slipper.getRemainingTurns());
            }
        }


        if(btl!=null) {
            EatFood eatFood = new EatFood(currentGame,btl);
            eatFood.bfsSearch();
            if(eatFood.isExistAnswer()){
                List<AttackGameState> states = eatFood.getAnswerList();
                if (states != null) {
                    if (states.size() > 0) {
                        myBeetleStrategyHashMap.put(btl.getId(),BeetleStrategy.EatFood);
                        myBeetleFoodPath.put(btl.getId(), states);
                    }
                }
            }
//            GameState rootState = new GameState(currentGame, btl.getDirection(), btl.getPosition().getX()
//                    , btl.getPosition().getY(), 0, foodList, foodAgeList
//                    ,trashList,trashAgeList,slipperList,slipperAgeList, null);
//            AStarSearch search = new AStarSearch(rootState);
//            if(search.isExistAnswer()) {
//                myBeetleStrategyHashMap.put(btl.getId(),BeetleStrategy.EatFood);
//                List<GameState> states = search.getAnswerList();
//                if (states != null) {
//                    if (states.size() > 0) {
//                        myBeetleFoodPath.put(btl.getId(), states);
//                    }
//                }
//            }
        }


//        System.out.println("AI2.findPathToFood: end");
    }
    private BeetleState calculateBeetleState(Beetle btl){
        CellState leftState =CellState.Blank;
        CellState rightState =CellState.Blank;
        CellState forwardState =CellState.Blank;
        Cell left,right;
        switch (btl.getDirection()){
            case Down:
                left = new Cell(btl.getPosition().getX()+1,btl.getPosition().getY()+1);
                right = new Cell(btl.getPosition().getX()+1,btl.getPosition().getY()-1);
                forwardState = seeForwardDown(btl.getPosition().getX(),btl.getPosition().getY());
                break;
            case Up:
                left = new Cell(btl.getPosition().getX()-1,btl.getPosition().getY()-1);
                right = new Cell(btl.getPosition().getX()-1,btl.getPosition().getY()+1);
                forwardState = seeForwardUp(btl.getPosition().getX(),btl.getPosition().getY());
                break;
            case Right:
                left = new Cell(btl.getPosition().getX()-1,btl.getPosition().getY()+1);
                right = new Cell(btl.getPosition().getX()+1,btl.getPosition().getY()+1);
                forwardState = seeForwardRight(btl.getPosition().getX(),btl.getPosition().getY());
                break;
            case Left:
                left = new Cell(btl.getPosition().getX()+1,btl.getPosition().getY()-1);
                right = new Cell(btl.getPosition().getX()-1,btl.getPosition().getY()-1);
                forwardState = seeForwardLeft(btl.getPosition().getX(),btl.getPosition().getY());
                break;
            default:
                left =new Cell(0,0);
                right =new Cell(0,0);
        }
        if(left.getX()==-1){
            left.setX(mapHeight-1);
        }else if(left.getX()==mapHeight){
            left.setX(0);
        }
        if(left.getY()==-1){
            left.setY(mapWidth-1);
        }else if(left.getY()==mapWidth){
            left.setY(0);
        }

        if(right.getX()==-1){
            right.setX(mapHeight-1);
        }else if(right.getX()==mapHeight){
            right.setX(0);
        }
        if(right.getY()==-1){
            right.setY(mapWidth-1);
        }else if(right.getY()==mapWidth){
            right.setY(0);
        }
//        System.out.println("AI2.calculateBeetleState: "+"btl.pos= "+btl.getPosition().getX() +","+btl.getPosition().getY());
//        System.out.println("AI2.calculateBeetleState: "+"left.pos= "+left.getX() +","+left.getY());
//        System.out.println("AI2.calculateBeetleState: "+"right.pos="+right.getX() +","+right.getY());

        Beetle leftBtl = (Beetle) currentGame.getMap().getCell(left.getX(),left.getY()).getBeetle();
        if(leftBtl!=null) {
            if (leftBtl.getType() == EntityType.Beetle) {
                if (leftBtl.getTeam() == this.teamId) {
                    leftState = CellState.Ally;
                } else {
                    leftState = CellState.Enemy;
                }
            }
        }
        Beetle rightBtl = (Beetle) currentGame.getMap().getCell(right.getX(),right.getY()).getBeetle();
        if(rightBtl!=null) {
            if (rightBtl.getType() == EntityType.Beetle) {
                if (rightBtl.getTeam() == this.teamId) {
                    rightState = CellState.Ally;
                } else {
                    rightState = CellState.Enemy;
                }
            }
        }
//        System.out.println("beetle.id= "+btl.getId()+"  state: "+BeetleState.fromAroundState(leftState,rightState,forwardState));
        return BeetleState.fromAroundState(leftState,rightState,forwardState);
    }
    private CellState seeForwardUp(int x,int y){
        CellState result =CellState.Ally;
        int index =x-1;
        while (index!=x){
            if(index==-1)
                index=mapHeight-1;
            Beetle btl = (Beetle) currentGame.getMap().getCell(index,y).getBeetle();
            if(btl!=null) {
                if (btl.getType() == EntityType.Beetle) {
                    if (btl.getTeam() == this.teamId) {
                        return CellState.Ally;
                    } else {
                        return CellState.Enemy;
                    }
                }
            }
            index-=1;
        }
        return result;
    }
    private CellState seeForwardRight(int x,int y){
        CellState result =CellState.Ally;
        int index =y+1;
        while (index!=y){
            if(index==mapWidth)
                index=0;
            Beetle btl = (Beetle) currentGame.getMap().getCell(x,index).getBeetle();
            if(btl!=null) {
                if (btl.getType() == EntityType.Beetle) {
                    if ( btl.getTeam() == this.teamId) {
                        return CellState.Ally;
                    } else {
                        return CellState.Enemy;
                    }
                }
            }
            index+=1;
        }
        return result;
    }
    private CellState seeForwardLeft(int x,int y){
        CellState result =CellState.Ally;
        int index =y-1;
        while (index!=y){
            if(index==-1)
                index=mapWidth-1;
            Beetle btl = (Beetle) currentGame.getMap().getCell(x,index).getBeetle();
            if(btl!=null) {
                if (btl.getType() == EntityType.Beetle) {
                    if (btl.getTeam() == this.teamId) {
                        return CellState.Ally;
                    } else {
                        return CellState.Enemy;
                    }
                }
            }
            index-=1;
        }
        return result;
    }
    private CellState seeForwardDown(int x,int y){
        CellState result =CellState.Ally;
        int index =x+1;
        while (index!=x){
            if(index==mapHeight)
                index=0;
            Beetle btl = (Beetle) currentGame.getMap().getCell(index,y).getBeetle();
            if(btl!=null) {
                if (btl.getType() == EntityType.Beetle) {
                    if (btl.getTeam() == this.teamId) {
                        return CellState.Ally;
                    } else {
                        return CellState.Enemy;
                    }
                }
            }
            index+=1;
        }
        return result;
    }
    private int getMaxPower(){
        int max =0;
        for (int i = 0; i < currentGame.getMap().getMyCells().length; i++) {
            Beetle btl =(Beetle)currentGame.getMap().getMyCells()[i].getBeetle();
            if(btl.getPower() >max);
                max =btl.getPower();
        }
        return max;
    }
    private int getBeetleWorth(Beetle btl,int maxPower){
        return btl.getPower();
//        int worth =maxPower+1 ;
//        BeetleStrategy strategy =myBeetleStrategyHashMap.get(btl.getId());
//        switch (strategy){
//            case TrashBreak:
//                worth*=7;
//                break;
//            case SlipperBreak:
//                worth*=6;
//                break;
//            case EnemyBreak:
//                worth*=5;
//                break;
//            case EatFood:
//                worth*=4;
//                break;
//            case EnemyFollow:
//                worth*=3;
//                break;
//            case Nothing:
//                worth*=2;
//                break;
//        }
//        if(!btl.is_sick())
//            worth*=8;
//        if(btl.has_winge())
//            worth*=9;
//        return worth+btl.getPower();

    }

    private Beetle getBestAttackerBeetle(){
        int power=-1;
        Beetle bestBeetle=null;
        for (int i = 0; i < currentGame.getMap().getMyCells().length; i++) {
            Beetle btl =(Beetle) currentGame.getMap().getMyCells()[i].getBeetle();
            if(btl!=null){
                if (btl.getPower()>power){
                    bestBeetle = btl;
                    power = btl.getPower();
                }
            }
        }
        return bestBeetle;
    }

    private Beetle getWeekestEnemy(Beetle attacker){
        HashMap<Integer,Integer> opponentDistance =new HashMap<>();
        for (int i = 0; i < currentGame.getMap().getOppCells().length; i++) {
            Beetle btl = (Beetle)currentGame.getMap().getOppCells()[i].getBeetle();
            if(btl!=null){
                opponentDistance.put(btl.getId(),calculateManhatanDistance(attacker.getPosition(),btl.getPosition()));
            }
        }
        LinkedHashMap<Integer,Integer> sortedDistance = sortHashMapByValues(opponentDistance);
        for (Integer key:sortedDistance.keySet()){
            if(((Beetle)currentGame.getMap().getEntity(key)).getPower()*currentGame.getConstants().getPowerRatio() < attacker.getPower()){
                return ((Beetle)currentGame.getMap().getEntity(key));
            }
        }
        return null;
    }
}
