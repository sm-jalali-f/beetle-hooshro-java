package client;

import client.model.*;

import java.util.Random;

/**
 * FirstAi class.
 * You should fill body of the method {@link #doTurn}.
 * Do not change name or modifiers of the methods or fields
 * and do not add constructor for this class.
 * You can add as many methods or fields as you want!
 * Use world parameter to access and modify game's
 * world!
 * See World interface for more details.
 */
public class FirstAi {

    public void doTurn(World game) {
        // fill this method, we've presented a stupid FirstAi for example!
        Random rand = new Random();


        Cell[][] cells = game.getMap().getCells();

        if (game.getCurrentTurn() == 0) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 3; k++) {
                        game.changeStrategy(BeetleType.LOW, CellState.values()[i], CellState.values()[j], CellState.values()[k], Move.turnLeft);
                        game.changeStrategy(BeetleType.HIGH, CellState.values()[i], CellState.values()[j], CellState.values()[k], Move.turnLeft);
                    }
                }
            }
        } else {

        }
        System.out.println("***************************"+ game.getCurrentTurn()+"***************************\n");
        System.out.println("map.width = "+game.getMap().getWidth());
        System.out.println("map.height= "+game.getMap().getHeight());
        System.out.println();
        for (int i = 0; i < game.getMap().getFoodCells().length; i++) {
            System.out.println("food.id= "+game.getMap().getFoodCells()[i].getItem().getId());
            System.out.println("food.pos= "+game.getMap().getFoodCells()[i].getItem().getPosition().getX()+","+game.getMap().getFoodCells()[i].getItem().getPosition().getY());
            System.out.println();
        }

        for (int i = 0; i < game.getMap().getMyCells().length; i++) {

            Cell cell = game.getMap().getMyCells()[i];
            if (cell != null) {
                Beetle btl = (Beetle)cell.getBeetle();
                if (btl != null) {
                    System.out.println("btl.id= "+btl.getId());
                    System.out.println("\nbtl.position= "+btl.getPosition().getX()+","+btl.getPosition().getY());
                    System.out.println("beetle.direction =" + btl.getDirection());
                    System.out.println();
                }
            }
        }

    }

}
