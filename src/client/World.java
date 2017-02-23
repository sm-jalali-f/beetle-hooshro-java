package client;

import client.model.*;

/**
 * Game Interface
 * At each turn, you are given an instance of the World, and you can call any of
 * the following methods to get information from the game, or do action on the
 * game. Please read documentation of any method you have problem with that.
 * Please do not change this class.
 */
public interface World {
    /**
     * Changing the strategy of the beetles with the given antenna type
     *
     * @param color Antenna type of the beetle (0 for single antenna and 1 for double antenna)
     * @param right Condition of the top-right neighbour
     * @param front Condition of the front
     * @param left Condition of the top-left neighbour
     * @param strategy The command given to the beetle (0 for turning right, 1 for going forward and 2 for turning left
     */

    void changeStrategy(BeetleType type, CellState right, CellState front, CellState left, Move move);

    /**
     * Give command to a specific Beetle with the given id
     *
     * @param id Beetle id
     * @param s The command given to the beetle(same as the previous method)
     */
    public void deterministicMove(Beetle beetle, Move move);

    /**
     * Changes antenna type of the given beetle
     *
     * @param id Beetle id
     * @param c Antenna type
     */
    public void changeType(Beetle beetle, BeetleType newType);

    /**
     * Number of turns passed from the beginning of the game
     *
     * @return current turn number
     */
    int getCurrentTurn();

    /**
     *
     * @return Team id
     */
    int getTeamID();

    /**
     *
     * @return User's score
     */
    int getMyScore();

    /**
     *
     * @return Opponent's score
     */
    int getOppScore();

    /**
     *
     * @return maximum turns
     */
    int getTotalTurns();

    /**
     *
     * @return remaining time of the turn
     */
    long getTurnRemainingTime();

    /**
     *
     * @return maximum time of a single turn
     */
    public long getTurnTotalTime();

    /**
     *
     * @return constants
     */
    Constants getConstants();





    /**
     *
     * @return Map of the game
     */
    Map getMap();

}
