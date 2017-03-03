package client.freezAi.models;

import client.model.CellState;

/**
 * Created by mohamad on 2/24/17.
 */
public enum BeetleState {

    Blank_Ally_Blank(0),Blank_Ally_Ally(1),Blank_Ally_Enemy(2),
    Ally_Ally_Blank(3),Ally_Ally_Ally(4),Ally_Ally_Enemy(5),
    Enemy_Ally_Blank(6),Enemy_Ally_Ally(7),Enemy_Ally_Enemy(8),
    Blank_Enemy_Blank(9),Blank_Enemy_Ally(10),Blank_Enemy_Enemy(11),
    Ally_Enemy_Blank(12),Ally_Enemy_Ally(13),Ally_Enemy_Enemy(14),
    Enemy_Enemy_Blank(15),Enemy_Enemy_Ally(16),Enemy_Enemy_Enemy(17);

    private int value;
    BeetleState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public static BeetleState fromAroundState(CellState left,CellState right,CellState forward){
//        System.out.println("left = [" + left + "], right = [" + right + "], forward = [" + forward + "]");
        switch (left){
            case Blank:
                switch (forward){
                    case Ally:
                        switch (right){
                            case Blank:
                                return BeetleState.Blank_Ally_Blank;
                            case Ally:
                                return BeetleState.Blank_Ally_Ally;
                            case Enemy:
                                return BeetleState.Blank_Ally_Enemy;
                        }
                        break;
                    case Enemy:
                        switch (right){
                            case Blank:
                                return BeetleState.Blank_Enemy_Blank;
                            case Ally:
                                return BeetleState.Blank_Enemy_Ally;
                            case Enemy:
                                return BeetleState.Blank_Enemy_Enemy;
                        }
                        break;
                }
                break;
            case Ally:
                switch (forward){
                    case Ally:
                        switch (right){
                            case Blank:
                                return BeetleState.Ally_Ally_Blank;
                            case Ally:
                                return BeetleState.Ally_Ally_Ally;
                            case Enemy:
                                return BeetleState.Ally_Ally_Enemy;
                        }
                        break;
                    case Enemy:
                        switch (right){
                            case Blank:
                                return BeetleState.Ally_Enemy_Blank;
                            case Ally:
                                return BeetleState.Ally_Enemy_Ally;
                            case Enemy:
                                return BeetleState.Ally_Enemy_Enemy;
                        }
                        break;
                }
                break;
            case Enemy:
                switch (forward){
                    case Ally:
                        switch (right){
                            case Blank:
                                return BeetleState.Enemy_Ally_Blank;
                            case Ally:
                                return BeetleState.Enemy_Ally_Ally;
                            case Enemy:
                                return BeetleState.Enemy_Ally_Enemy;
                        }
                        break;
                    case Enemy:
                        switch (right){
                            case Blank:
                                return BeetleState.Enemy_Enemy_Blank;
                            case Ally:
                                return BeetleState.Enemy_Enemy_Ally;
                            case Enemy:
                                return BeetleState.Enemy_Enemy_Enemy;
                        }
                        break;
                }
                break;
        }
        return BeetleState.Blank_Ally_Blank;
    }

    public static CellState getRight(Integer key) {
        int rem = key % 3 ;
        if(rem  ==0)
            return CellState.Blank;
        else if(rem ==1)
            return CellState.Ally;
        else
            return CellState.Enemy;
    }
    public static CellState getLeft(Integer key) {
        if((key < 3) ||(key<12 && key>8)){
            return CellState.Blank;
        }else if((key < 6 && key>2 ) ||(key<15 && key>11)){
            return CellState.Ally;
        }else{
            return CellState.Enemy;
        }
    }
    public static CellState getFront(Integer key) {
        if(key<9)
            return CellState.Ally;
        else
            return CellState.Enemy;
    }
    public static BeetleState fromValue(Integer value) {
        switch (value){
            case 0:
                return Blank_Ally_Blank;
            case 1:
                return Blank_Ally_Ally;
            case 2:
                return Blank_Ally_Enemy;
            case 3:
                return Ally_Ally_Blank;
            case 4:
                return Ally_Ally_Ally;
            case 5:
                return Ally_Ally_Enemy;
            case 6:
                return Enemy_Ally_Blank;
            case 7:
                return Enemy_Ally_Ally;
            case 8:
                return Enemy_Ally_Enemy;
            case 9:
                return Blank_Enemy_Blank;
            case 10:
                return Blank_Enemy_Ally;
            case 11:
                return Blank_Enemy_Enemy;
            case 12:
                return Ally_Enemy_Blank;
            case 13:
                return Ally_Enemy_Ally;
            case 14:
                return Ally_Enemy_Enemy;
            case 15:
                return Enemy_Enemy_Blank;
            case 16:
                return Enemy_Enemy_Ally;
            case 17:
                return Enemy_Enemy_Enemy;
        }
        return Enemy_Enemy_Enemy;
    }
}
