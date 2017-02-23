package client.model;

import java.util.ArrayList;

/**
 * Created by Future on 2/7/17.
 */
class Change {
    private char type;
    private ArrayList<ArrayList<Integer>> args;

    char getType() {
        return type;
    }

    ArrayList<ArrayList<Integer>> getArgs() {
        return args;
    }
}
