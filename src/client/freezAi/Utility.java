package client.freezAi;

import client.model.Cell;

/**
 * Created by mohamad on 2/25/17.
 */
public class Utility {
    
    public static  boolean isUnderSlipper(Cell btlCell,Cell slipperCell,int mapHeight,int mapWidth){
        Cell leftTopCell = new Cell(slipperCell.getX()-1,slipperCell.getY()-1);
        if(leftTopCell.getX()==-1)
            leftTopCell.setX(mapHeight-1);
        if(leftTopCell.getY()==-1)
            leftTopCell.setY(mapWidth-1);
        Cell topCell = new Cell(slipperCell.getX()-1,slipperCell.getY());
        if(topCell.getX()==-1)
            topCell.setX(mapHeight-1);
        Cell rightTopCell = new Cell(slipperCell.getX()-1,slipperCell.getY()+1);
        if(rightTopCell.getX()==-1)
            rightTopCell.setX(mapHeight-1);
        if(rightTopCell.getY()==mapWidth)
            rightTopCell.setY(0);
        Cell rightCell = new Cell(slipperCell.getX(),slipperCell.getY()+1);
        if(rightCell.getY()==mapWidth)
            rightCell.setY(0);
        Cell rightBottomCell = new Cell(slipperCell.getX()+1,slipperCell.getY()+1);
        if(rightBottomCell.getX()==mapHeight)
            rightBottomCell.setX(0);
        if(rightBottomCell.getY()==mapWidth)
            rightBottomCell.setY(0);
        Cell bottomCell = new Cell(slipperCell.getX()+1,slipperCell.getY());
        if(bottomCell.getX()==mapHeight)
            bottomCell.setX(0);
        Cell leftBottomCell = new Cell(slipperCell.getX()+1,slipperCell.getY()-1);
        if(leftBottomCell.getX()==mapHeight)
            leftBottomCell.setX(0);
        if(leftBottomCell.getY()==-1)
            leftBottomCell.setY(mapWidth-1);
        Cell leftCell = new Cell(slipperCell.getX(),slipperCell.getY()-1);
        if(leftCell.getY()==-1)
            leftCell.setY(mapWidth-1);
        if((btlCell.getX()== leftTopCell.getX() && btlCell.getY()== leftTopCell.getY() )||
                (btlCell.getX()== topCell.getX() && btlCell.getY()== topCell.getY() ) ||
                (btlCell.getX()== rightTopCell.getX() && btlCell.getY()== rightTopCell.getY() ) ||
                (btlCell.getX()== rightCell.getX() && btlCell.getY()== rightCell.getY() )||
                (btlCell.getX()== rightBottomCell.getX() && btlCell.getY()== rightBottomCell.getY() )||
                (btlCell.getX()== bottomCell.getX() && btlCell.getY()== bottomCell.getY() ) ||
                (btlCell.getX()== leftBottomCell.getX() && btlCell.getY()== leftBottomCell.getY() ) ||
                (btlCell.getX()== leftCell.getX() && btlCell.getY()== leftCell.getY() )){
            return true;
        }
        return false;
    }
    static public Cell getLeft(Cell cell,int mapWidth){
        int tempX = cell.getX();
        int tempY = cell.getY()-1;
        if(tempY==-1)
            tempY =mapWidth-1;
        return new Cell(tempX,tempY);
    }
    static public Cell getRight(Cell cell,int mapWidth){
        int tempX = cell.getX();
        int tempY = cell.getY()+1;
        if(tempY==mapWidth)
            tempY =0;
        return new Cell(tempX,tempY);
    }
    static public Cell getTop(Cell cell,int mapHeight){
        int tempX = cell.getX()-1;
        int tempY = cell.getY();
        if(tempX==-1)
            tempY =mapHeight-1;
        return new Cell(tempX,tempY);
    }
    static public Cell getBottom(Cell cell,int mapHeight){
        int tempX = cell.getX()+1;
        int tempY = cell.getY();
        if(tempX==mapHeight)
            tempY =0;
        return new Cell(tempX,tempY);
    }
}
