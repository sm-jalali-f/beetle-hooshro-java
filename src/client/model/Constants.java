package client.model;

/**
 * Created by Future on 2/10/17.
 */
public class Constants {
    private double foodProb;
    private double trashProb;
    private double netProb;
    private int netValidTime;
    private int turnTimeout;
    private int colorCost;
    private int sickCost;
    private int updateCost;
    private int detMoveCost;
    private int killQueenScore;
    private int killBothQueenScore;
    private int killFishScore;
    private int queenCollisionScore;
    private int fishFoodScore;
    private int queenFoodScore;
    private int sickLifeTime;
    private double powerRatio;
    private double endRatio;
    private int disobeyNum;
    private int foodValidTime;
    private int trashValidTime;
    private int totalTurns;


    public double getFoodProb() {
        return foodProb;
    }

    void setFoodProb(double foodProb) {
        this.foodProb = foodProb;
    }

    public double getTrashProb() {
        return trashProb;
    }

    void setTrashProb(double trashProb) {
        this.trashProb = trashProb;
    }

    public double getNetProb() {
        return netProb;
    }

    void setNetProb(double netProb) {
        this.netProb = netProb;
    }

    public int getTurnTimeout() {
        return turnTimeout;
    }

    void setTurnTimeout(int turnTimeout) {
        this.turnTimeout = turnTimeout;
    }

    public int getColorCost() {
        return colorCost;
    }

    void setColorCost(int colorCost) {
        this.colorCost = colorCost;
    }

    public int getSickCost() {
        return sickCost;
    }

    void setSickCost(int sickCost) {
        this.sickCost = sickCost;
    }

    public int getUpdateCost() {
        return updateCost;
    }

    void setUpdateCost(int updateCost) {
        this.updateCost = updateCost;
    }

    public int getDetMoveCost() {
        return detMoveCost;
    }

    void setDetMoveCost(int detMoveCost) {
        this.detMoveCost = detMoveCost;
    }

    public int getKillQueenScore() {
        return killQueenScore;
    }

    void setKillQueenScore(int killQueenScore) {
        this.killQueenScore = killQueenScore;
    }

    public int getKillBothQueenScore() {
        return killBothQueenScore;
    }

    void setKillBothQueenScore(int killBothQueenScore) {
        this.killBothQueenScore = killBothQueenScore;
    }

    public int getKillFishScore() {
        return killFishScore;
    }

    void setKillFishScore(int killFishScore) {
        this.killFishScore = killFishScore;
    }

    public int getFishFoodScore() {
        return fishFoodScore;
    }

    void setFishFoodScore(int fishFoodScore) {
        this.fishFoodScore = fishFoodScore;
    }

    public int getQueenFoodScore() {
        return queenFoodScore;
    }

    void setQueenFoodScore(int queenFoodScore) {
        this.queenFoodScore = queenFoodScore;
    }

    public int getSickLifeTime() {
        return sickLifeTime;
    }

    void setSickLifeTime(int sickLifeTime) {
        this.sickLifeTime = sickLifeTime;
    }

    public double getPowerRatio() {
        return powerRatio;
    }

    void setPowerRatio(double powerRatio) {
        this.powerRatio = powerRatio;
    }

    public double getEndRatio() {
        return endRatio;
    }

    void setEndRatio(double endRatio) {
        this.endRatio = endRatio;
    }

    public int getDisobeyNum() {
        return disobeyNum;
    }

    void setDisobeyNum(int disobeyNum) {
        this.disobeyNum = disobeyNum;
    }

    public int getFoodValidTime() {
        return foodValidTime;
    }

    void setFoodValidTime(int foodValidTime) {
        this.foodValidTime = foodValidTime;
    }

    public int getTrashValidTime() {
        return trashValidTime;
    }

    void setTrashValidTime(int trashValidTime) {
        this.trashValidTime = trashValidTime;
    }

    public int getNetValidTime() {
        return netValidTime;
    }

    void setNetValidTime(int netValidTime) {
        this.netValidTime = netValidTime;
    }

    void setQueenCollisionScore(int queenCollisionScore) {
        this.queenCollisionScore = queenCollisionScore;
    }

    public int getQueenCollisionScore() {
        return queenCollisionScore;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    void setTotalTurns(int totalTurns) {
        this.totalTurns = totalTurns;
    }
}
