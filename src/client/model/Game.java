package client.model;


import client.World;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import common.model.Event;
import common.network.data.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

public class Game implements World
{
    private int currentTurn = 0;
    private int teamID;
    private int myScore;
    private int oppScore;

    private Constants constants;

    private HashMap<Integer, Cell> idMap = new HashMap<>();
    private HashMap<Integer, Entity> infoMap = new HashMap<>(); // This field is not usefull for user

    private Consumer<Message> sender;
    private Map map;
    private long turnStartTime;

    public Game(Consumer<Message> sender)
    {
        this.sender = sender;
    }


    public void changeStrategy(BeetleType type, CellState right, CellState front, CellState left, Move newStrategy)
    {
        Event event = new Event("s", new Object[]{type.ordinal(), right.ordinal(), front.ordinal(), left.ordinal(), newStrategy.ordinal()});
        sender.accept(new Message(Event.EVENT, event));
    }

    public void deterministicMove(Beetle beetle, Move move)
    {
        Event event = new Event("m", new Object[]{beetle.getId(), move.ordinal()});
        sender.accept(new Message(Event.EVENT, event));
    }

    public void changeType(Beetle beetle, BeetleType newType)
    {
        Event event = new Event("c", new Object[]{beetle.getId(), newType.ordinal()});
        sender.accept(new Message(Event.EVENT, event));
    }

    public void handleInitMessage(Message msg)
    {
        JsonArray constants = msg.args.get(7).getAsJsonArray();
        this.setConstants(constants);

        teamID = msg.args.get(0).getAsInt();

        JsonArray size = msg.args.get(1).getAsJsonArray();
        int width = size.get(0).getAsInt();
        int height = size.get(1).getAsInt();

        Cell[][] cells = new Cell[height][width];
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                cells[i][j] = new Cell(i, j);
            }
        }
        map = new Map(cells, width, height);


        JsonArray Beetles = msg.args.get(2).getAsJsonArray();
        Cell[][] beetles = new Cell[2][];
        ArrayList<Cell> myBeetles = new ArrayList<>();
        ArrayList<Cell> oppBeetles = new ArrayList<>();

        int myBeetle = 0;
        int oppBeetle = 0;
        for (int i = 0; i < Beetles.size(); i++)
        {
            int cellX, cellY;
            JsonArray beetleInfo = Beetles.get(i).getAsJsonArray();
            int id = beetleInfo.get(0).getAsInt();

            cellX = beetleInfo.get(1).getAsInt();
            cellY = beetleInfo.get(2).getAsInt();

            Cell theChosenCell = map.getCell(cellX, cellY);
            theChosenCell.addBeetleInfo(beetleInfo);

            idMap.put(id, theChosenCell);
            infoMap.put(id, theChosenCell.getBeetle());

            if (teamID == beetleInfo.get(7).getAsInt())
            {
                myBeetles.add(theChosenCell);
            } else
            {
                oppBeetles.add(theChosenCell);
            }
        }
        Cell[] mBeetles = new Cell[myBeetles.size()];
        Cell[] oBeetles = new Cell[oppBeetles.size()];

        mBeetles = myBeetles.toArray(mBeetles);
        oBeetles = oppBeetles.toArray(oBeetles);

        beetles[0] = mBeetles;
        beetles[1] = oBeetles;

        map.setBeetles(beetles);

        JsonArray foods = msg.args.get(3).getAsJsonArray();
        Cell[] foodCells = new Cell[foods.size()];
        for (int i = 0; i < foods.size(); i++)
        {
            JsonArray foodInfo = foods.get(i).getAsJsonArray();
            int id = foodInfo.get(0).getAsInt();
            int cellX = foodInfo.get(1).getAsInt();
            int cellY = foodInfo.get(2).getAsInt();

            Cell theChosenCell = map.getCell(cellX, cellY);
            theChosenCell.addFood(id, this.constants.getFoodValidTime());
            foodCells[i] = theChosenCell;

            idMap.put(id, theChosenCell);
            infoMap.put(id, theChosenCell.getItem());
        }
        map.setFoodCells(foodCells);

        JsonArray trashes = msg.args.get(4).getAsJsonArray();
        Cell[] trashCells = new Cell[trashes.size()];
        for (int i = 0; i < trashes.size(); i++)
        {
            JsonArray trashInfo = trashes.get(i).getAsJsonArray();
            int id = trashInfo.get(0).getAsInt();
            int cellX = trashInfo.get(1).getAsInt();
            int cellY = trashInfo.get(2).getAsInt();

            Cell theChosenCell = cells[cellX][cellY];
            theChosenCell.addTrash(id, this.constants.getTrashValidTime());
            trashCells[i] = theChosenCell;

            idMap.put(id, theChosenCell);
            infoMap.put(id, theChosenCell.getItem());
        }
        map.setTrashCells(trashCells);

        JsonArray slippers = msg.args.get(5).getAsJsonArray();
        Cell[] slipperCells = new Cell[slippers.size()];
        for (int i = 0; i < slippers.size(); i++)
        {
            JsonArray slipperInfo = slippers.get(i).getAsJsonArray();
            int id = slipperInfo.get(0).getAsInt();
            int cellX = slipperInfo.get(1).getAsInt();
            int cellY = slipperInfo.get(2).getAsInt();

            Cell theChosenCell = cells[cellX][cellY];
            theChosenCell.addSlipper(id, this.constants.getNetValidTime());
            slipperCells[i] = theChosenCell;

            idMap.put(id, theChosenCell);
            infoMap.put(id, theChosenCell.getSlipper());
        }
        map.setSlipperCells(slipperCells);

        JsonArray teleports = msg.args.get(6).getAsJsonArray();
        Cell[] teleportCells = new Cell[teleports.size()];
        for (int i = 0; i < teleports.size(); i++)
        {
            JsonArray teleportInfo = teleports.get(i).getAsJsonArray();
            int id = teleportInfo.get(0).getAsInt();
            int cellX = teleportInfo.get(1).getAsInt();
            int cellY = teleportInfo.get(2).getAsInt();

            Cell theChosenCell = cells[cellX][cellY];
            theChosenCell.addTeleport(id, teleportInfo.get(3).getAsInt());
            teleportCells[i] = theChosenCell;

            idMap.put(id, theChosenCell);
            infoMap.put(id, theChosenCell.getTeleport());
        }
        map.setTeleportCells(teleportCells);
    }

    public void handleTurnMessage(Message msg)
    {
        currentTurn = msg.args.get(0).getAsInt();

        JsonArray scores = msg.args.get(1).getAsJsonArray();
        myScore = scores.get(teamID).getAsInt();
        oppScore = scores.get(1 - teamID).getAsInt();

        JsonArray allChanges = msg.args.get(2).getAsJsonArray();
        for (int i = 0; i < allChanges.size(); i++)
        {
            Gson gson = new Gson();
            JsonObject changes = allChanges.get(i).getAsJsonObject();
            String jsonString = changes.toString();
            Change change = gson.fromJson(jsonString, Change.class);
            char type = change.getType();
            if (type == 'a')
            {
                ArrayList<ArrayList<Integer>> allAdds = change.getArgs();
                for (int j = 0; j < allAdds.size(); j++)
                {
                    ArrayList<Integer> addChange = allAdds.get(j);
                    switch (addChange.get(1))
                    {
                        case 0:
                            addBeetle(addChange);
                            break;
                        case 1:
                            addFood(addChange);
                            break;
                        case 2:
                            addTrash(addChange);
                            break;
                        case 3:
                            addSlipper(addChange);
                            break;
                    }
                }
            } else if (type == 'd')
            {
                ArrayList<ArrayList<Integer>> allDeletes = change.getArgs();
                for (int j = 0; j < allDeletes.size(); j++)
                {
                    ArrayList<Integer> deleteChange = allDeletes.get(j);
                    delete(deleteChange);
                }
            } else if (type == 'm')
            {
                ArrayList<ArrayList<Integer>> allMoves = change.getArgs();
                for (int j = 0; j < allMoves.size(); j++)
                {
                    ArrayList<Integer> moveChange = allMoves.get(j);
                    moveBeetle(moveChange);
                }
            } else if (type == 'c')
            {
                ArrayList<ArrayList<Integer>> allAlters = change.getArgs();
                for (int j = 0; j < allAlters.size(); j++)
                {
                    ArrayList<Integer> alter = allAlters.get(j);
                    if (alter.size() == 5)
                    {
                        beetleAlter(alter);
                    } else
                    {
                        itemAlter(alter);
                    }
                }
            }
        }
        handleFinalChanges();
        turnStartTime = System.currentTimeMillis();
    }

    private void handleFinalChanges()
    {
        map.setIdMap(idMap);
        handleTeleports();
        handleEntityCells();
        handleFoodRemainings();
        handleTrashRemainings();
        handleSlipperRemainings();
    }

    private void handleTeleports()
    {
        Cell[] teleportCells = map.getTeleportCells();
        for (Cell cell : teleportCells)
        {
            Teleport theChosenTeleport = (Teleport) cell.getTeleport();
            int targetId = theChosenTeleport.getTargetId();
            Teleport targetTeleport = (Teleport) idMap.get(targetId).getTeleport();
            theChosenTeleport.setPair(targetTeleport);
        }
    }

    private void handleEntityCells()
    {
        for (Integer id : idMap.keySet())
        {

            Entity theChosenEntity = map.getEntity(id);
            theChosenEntity.setCell(idMap.get(id));
        }
    }

    private void handleFoodRemainings()
    {
        Cell[] foodCells = map.getFoodCells();
        for (Cell cell : foodCells)
        {
            Food food = (Food) cell.getFoodEntity();
            food.setRemainingTurn(food.getRemainingTurns() - 1);
        }
    }

    private void handleTrashRemainings()
    {
        Cell[] trashCells = map.getTrashCells();
        for (Cell cell : trashCells)
        {
            Trash trash = (Trash) cell.getTrashEntity();
            trash.setRemainingTurn(trash.getRemainingTurns() - 1);
        }
    }

    private void handleSlipperRemainings()
    {
        Cell[] slipperCells = map.getSlipperCells();
        for (Cell cell : slipperCells)
        {
            Slipper slipper = (Slipper) cell.getSlipper();
            slipper.setRemainingTurn(slipper.getRemainingTurns() - 1);
        }
    }

    private void itemAlter(ArrayList<Integer> changes)
    {
        int id = changes.get(0);
        int x = changes.get(1);
        int y = changes.get(2);

        Cell theChosenCell = idMap.get(id);
        Cell[][] cells = map.getCells();
        Cell targetCell = cells[x][y];

        targetCell.receiveInfo(theChosenCell.getItem());
        idMap.put(id, targetCell);
        if (targetCell.getItem().getId() == theChosenCell.getItem().getId())
        {
            theChosenCell.setItemEntity(null);
        }

        if (map.getEntityType(id) == EntityType.Food)
        {
            Cell[] foods = map.getFoodCells();
            ArrayList<Cell> foodList = new ArrayList<Cell>(Arrays.asList(foods));
            foodList.remove(theChosenCell);
            foodList.add(targetCell);
            Cell[] tempCell = new Cell[foodList.size()];
            foods = foodList.toArray(tempCell);
            map.setFoodCells(foods);
        } else
        {
            Cell[] trashes = map.getTrashCells();
            ArrayList<Cell> trashList = new ArrayList<Cell>(Arrays.asList(trashes));
            trashList.remove(theChosenCell);
            trashList.add(targetCell);
            Cell[] tempCell = new Cell[trashList.size()];
            trashes = trashList.toArray(tempCell);
            map.setTrashCells(trashes);
        }
    }

    private void delete(ArrayList<Integer> changes)
    {
        int id = changes.get(0);

        Cell theChosenCell = idMap.get(id);
        idMap.remove(id);

        Entity theChosenInfo = infoMap.get(id);
        infoMap.remove(id);

        if (theChosenInfo instanceof Beetle)
        {
            Beetle chosenBeetleInfo = (Beetle) theChosenInfo;
            if (chosenBeetleInfo.getTeam() == teamID)
            {
                Cell[] beetles = map.getMyCells();
                ArrayList<Cell> beetleList = new ArrayList<Cell>(Arrays.asList(beetles));
                beetleList.remove(theChosenCell);
                Cell[] tempCell = new Cell[beetleList.size()];
                beetles = beetleList.toArray(tempCell);
                map.setMyCells(beetles);
            } else
            {
                Cell[] beetles = map.getOppCells();
                ArrayList<Cell> beetleList = new ArrayList<Cell>(Arrays.asList(beetles));
                beetleList.remove(theChosenCell);
                Cell[] tempCell = new Cell[beetleList.size()];
                beetles = beetleList.toArray(tempCell);
                map.setOppCells(beetles);
            }
            theChosenCell.clear();
        } else if (theChosenInfo instanceof Food)
        {
            Cell[] foods = map.getFoodCells();
            ArrayList<Cell> foodList = new ArrayList<Cell>(Arrays.asList(foods));
            foodList.remove(theChosenCell);
            Cell[] tempCell = new Cell[foodList.size()];
            foods = foodList.toArray(tempCell);
            map.setFoodCells(foods);
            theChosenCell.clear();
        } else if (theChosenInfo instanceof Trash)
        {
            Cell[] trashes = map.getTrashCells();
            ArrayList<Cell> trashList = new ArrayList<Cell>(Arrays.asList(trashes));
            trashList.remove(theChosenCell);
            Cell[] tempCell = new Cell[trashList.size()];
            trashes = trashList.toArray(tempCell);
            map.setTrashCells(trashes);
            theChosenCell.clear();
        } else if (theChosenInfo instanceof Slipper)
        {
            Cell[] slippers = map.getSlipperCells();
            ArrayList<Cell> slipperList = new ArrayList<Cell>(Arrays.asList(slippers));
            slipperList.remove(theChosenCell);
            Cell[] tempCell = new Cell[slipperList.size()];
            slippers = slipperList.toArray(tempCell);
            map.setSlipperCells(slippers);
            theChosenCell.cleanSlipper();
        }
    }

    private void moveBeetle(ArrayList<Integer> changes)
    {
        int id = changes.get(0);
        int move = changes.get(1);
        Cell theChosenCell = idMap.get(id);
        Beetle theChosenInfo = null;
        theChosenInfo = (Beetle) (infoMap.get(id));
        switch (move)
        {
            case 0:
                theChosenInfo.setDirection((theChosenInfo.getDirectionInt() + 3) % 4);
                break;
            case 1:
                int cellX = nextX(theChosenCell, theChosenInfo.getDirectionInt());
                int cellY = nextY(theChosenCell, theChosenInfo.getDirectionInt());
                Cell targetCell = map.getCell(cellX, cellY);
                targetCell.receiveInfo(theChosenInfo);
                idMap.put(id, targetCell);
                if (targetCell.getBeetle().getId() == theChosenCell.getBeetle().getId())
                {
                    theChosenCell.clear();
                }
                Beetle beetle = (Beetle) map.getEntity(id);
                beetle.setPower(beetle.getPower() + 1);
                int team = ((Beetle) (map.getEntity(id))).getTeam();
                ArrayList<Cell> beetleList;
                if (team == teamID)
                {
                    Cell[] beetles = map.getMyCells();
                    beetleList = new ArrayList<Cell>(Arrays.asList(beetles));
                    beetleList.remove(theChosenCell);
                    beetleList.add(targetCell);
                    Cell[] tempCell = new Cell[beetleList.size()];
                    beetles = beetleList.toArray(tempCell);
                    map.setMyCells(beetles);
                } else
                {
                    Cell[] beetles = map.getOppCells();
                    beetleList = new ArrayList<Cell>(Arrays.asList(beetles));
                    beetleList.remove(theChosenCell);
                    beetleList.add(targetCell);
                    Cell[] tempCell = new Cell[beetleList.size()];
                    beetles = beetleList.toArray(tempCell);
                    map.setOppCells(beetles);
                }
                break;
            case 2:
                theChosenInfo.setDirection((theChosenInfo.getDirectionInt() + 1) % 4);
                break;
        }
    }

    private int nextX(Cell cell, int dir)
    {
        int direction = dir;
        int x = cell.getX();
        switch (direction)
        {
            case 3:
                x = (x + 1) % map.getHeight();
                break;
            case 1:
                x = (x + map.getHeight() - 1) % map.getHeight();
                break;
        }
        return x;
    }

    private int nextY(Cell cell, int dir)
    {
        int direction = dir;
        int y = cell.getY();
        switch (direction)
        {
            case 2:
                y = (y + map.getWidth() - 1) % map.getWidth();
                break;
            case 0:
                y = (y + 1) % map.getWidth();
                break;
        }
        return y;
    }

    private void beetleAlter(ArrayList<Integer> changes)
    {
        int id = changes.get(0);
        int newX = changes.get(1);
        int newY = changes.get(2);
        int color = changes.get(3);
        int sick = changes.get(4);

        Cell theChosenCell = idMap.get(id);
        Beetle theChosenInfo = (Beetle) infoMap.get(id);
        Cell targetCell = map.getCell(newX, newY);
        theChosenInfo.setColor(color);
        theChosenInfo.setSick(sick);

        targetCell.receiveInfo(theChosenInfo);
        idMap.put(id, targetCell);
        if (targetCell != theChosenCell)
        {
            if (targetCell.getBeetle().getId() == theChosenCell.getBeetle().getId())
            {
                theChosenCell.clear();
            }

            int team = ((Beetle) (map.getEntity(id))).getTeam();
            ArrayList<Cell> beetleList;
            if (team == teamID)
            {
                Cell[] beetles = map.getMyCells();
                beetleList = new ArrayList<Cell>(Arrays.asList(beetles));
                beetleList.remove(theChosenCell);
                beetleList.add(targetCell);
                Cell[] tempCell = new Cell[beetleList.size()];
                beetles = beetleList.toArray(tempCell);
                map.setMyCells(beetles);
            } else
            {
                Cell[] beetles = map.getOppCells();
                beetleList = new ArrayList<Cell>(Arrays.asList(beetles));
                beetleList.remove(theChosenCell);
                beetleList.add(targetCell);
                Cell[] tempCell = new Cell[beetleList.size()];
                beetles = beetleList.toArray(tempCell);
                map.setOppCells(beetles);
            }
        }
    }

    private void addBeetle(ArrayList<Integer> changes)
    {
        ArrayList<Cell> beetleList;
        int id = changes.get(0);
        int cellX = changes.get(2);
        int cellY = changes.get(3);
        int direction = changes.get(4);
        int color = changes.get(5);
        int queen = changes.get(6);
        int team = changes.get(7);

        Cell theChosenCell = map.getCell(cellX, cellY);
        theChosenCell.addBeetleInfo(id, direction, color, queen, team);

        idMap.put(id, theChosenCell);
        infoMap.put(id, theChosenCell.getBeetle());

        if (team == teamID)
        {
            Cell[] beetles = map.getMyCells();
            beetleList = new ArrayList<Cell>(Arrays.asList(beetles));
            beetleList.add(theChosenCell);
            Cell[] tempCell = new Cell[beetleList.size()];
            beetles = beetleList.toArray(tempCell);
            map.setMyCells(beetles);
        } else
        {
            Cell[] beetles = map.getOppCells();
            beetleList = new ArrayList<Cell>(Arrays.asList(beetles));
            beetleList.add(theChosenCell);
            Cell[] tempCell = new Cell[beetleList.size()];
            beetles = beetleList.toArray(tempCell);
            map.setOppCells(beetles);
        }
    }

    private void addFood(ArrayList<Integer> changes)
    {
        int id = changes.get(0);
        int cellX = changes.get(2);
        int cellY = changes.get(3);

        Cell theChosenCell = map.getCell(cellX, cellY);
        theChosenCell.addFood(id, constants.getFoodValidTime());

        idMap.put(id, theChosenCell);
        infoMap.put(id, theChosenCell.getItem());

        Cell[] foods = map.getFoodCells();
        ArrayList<Cell> foodList = new ArrayList<Cell>(Arrays.asList(foods));
        foodList.add(theChosenCell);
        Cell[] tempCell = new Cell[foodList.size()];
        foods = foodList.toArray(tempCell);
        map.setFoodCells(foods);
    }

    private void addTrash(ArrayList<Integer> changes)
    {
        int id = changes.get(0);
        int cellX = changes.get(2);
        int cellY = changes.get(3);

        Cell theChosenCell = map.getCell(cellX, cellY);
        theChosenCell.addTrash(id, constants.getTrashValidTime());

        idMap.put(id, theChosenCell);
        infoMap.put(id, theChosenCell.getItem());

        Cell[] trashes = map.getTrashCells();
        ArrayList<Cell> trashList = new ArrayList<Cell>(Arrays.asList(trashes));
        trashList.add(theChosenCell);
        Cell[] tempCell = new Cell[trashList.size()];
        trashes = trashList.toArray(tempCell);
        map.setTrashCells(trashes);
    }

    private void addSlipper(ArrayList<Integer> changes)
    {
        int id = changes.get(0);
        int cellX = changes.get(2);
        int cellY = changes.get(3);

        Cell theChosenCell = map.getCell(cellX, cellY);
        theChosenCell.addSlipper(id, constants.getNetValidTime());

        idMap.put(id, theChosenCell);
        infoMap.put(id, theChosenCell.getSlipper());

        Cell[] slippers = map.getSlipperCells();
        ArrayList<Cell> slipperList = new ArrayList<Cell>(Arrays.asList(slippers));
        slipperList.add(theChosenCell);
        Cell[] tempCell = new Cell[slipperList.size()];
        slippers = slipperList.toArray(tempCell);
        map.setSlipperCells(slippers);
    }

    public Map getMap()
    {
        return map;
    }

    public int getCurrentTurn()
    {
        return currentTurn;
    }

    public int getTeamID()
    {
        return teamID;
    }

    public int getMyScore()
    {
        return myScore;
    }

    public int getOppScore()
    {
        return oppScore;
    }

    @Override
    public int getTotalTurns()
    {
        return constants.getTotalTurns();
    }

    @Override
    public long getTurnRemainingTime()
    {
        return getTurnTotalTime() - System.currentTimeMillis() + turnStartTime;
    }

    @Override
    public long getTurnTotalTime()
    {
        return constants.getTurnTimeout();
    }

    @Override
    public Constants getConstants()
    {
        return this.constants;
    }

    private void setConstants(JsonArray constants)
    {
        this.constants = new Constants();
        this.constants.setTurnTimeout((int) constants.get(0).getAsDouble());
        this.constants.setFoodProb(constants.get(1).getAsDouble());
        this.constants.setTrashProb(constants.get(2).getAsDouble());
        this.constants.setNetProb(constants.get(3).getAsDouble());
        this.constants.setNetValidTime((int) constants.get(4).getAsDouble());
        this.constants.setColorCost((int) constants.get(5).getAsDouble());
        this.constants.setSickCost((int) constants.get(6).getAsDouble());
        this.constants.setUpdateCost((int) constants.get(7).getAsDouble());
        this.constants.setDetMoveCost((int) constants.get(8).getAsDouble());
        this.constants.setKillQueenScore((int) constants.get(9).getAsDouble());
        this.constants.setKillBothQueenScore((int) constants.get(10).getAsDouble());
        this.constants.setKillFishScore((int) constants.get(11).getAsDouble());
        this.constants.setQueenCollisionScore((int) constants.get(12).getAsDouble());
        this.constants.setFishFoodScore((int) constants.get(13).getAsDouble());
        this.constants.setQueenFoodScore((int) constants.get(14).getAsDouble());
        this.constants.setSickLifeTime((int) constants.get(15).getAsDouble());
        this.constants.setPowerRatio(constants.get(16).getAsDouble());
        this.constants.setEndRatio(constants.get(17).getAsDouble());
        this.constants.setDisobeyNum((int) constants.get(18).getAsDouble());
        this.constants.setFoodValidTime((int) constants.get(19).getAsDouble());
        this.constants.setTrashValidTime((int) constants.get(20).getAsDouble());
        this.constants.setTotalTurns((int) constants.get(21).getAsDouble());
        this.constants.setDeterministicMoveLimit((int) constants.get(22).getAsDouble());
        this.constants.setChangeTypeLimit((int) constants.get(23).getAsDouble());
    }
}