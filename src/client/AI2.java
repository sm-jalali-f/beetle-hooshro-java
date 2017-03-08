package client;

import client.model.*;

import java.util.Random;

/**
 * AI2 class.
 * You should fill body of the method {@link #doTurn}.
 * Do not change name or modifiers of the methods or fields
 * and do not add constructor for this class.
 * You can add as many methods or fields as you want!
 * Use world parameter to access and modify game's
 * world!
 * See World interface for more details.
 */
public class AI2
{

    public void doTurn(World game)
    {
        // fill this method, we've presented a stupid AI2 for example!
        Random rand = new Random();

        Map map = game.getMap();
        Cell[] cells = map.getMyCells();
        for (Cell cell : cells)
        {
            Beetle beetle = (Beetle) cell.getBeetle();
            game.deterministicMove(beetle, Move.stepForward);
            if (beetle.getBeetleType() == BeetleType.HIGH)
            {
                game.changeType(beetle, BeetleType.LOW);
            } else
            {
                game.changeType(beetle, BeetleType.HIGH);
            }
        }

        if (game.getCurrentTurn() == 0)
        {
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 2; j++)
                {
                    for (int k = 0; k < 3; k++)
                    {
                        game.changeStrategy(BeetleType.LOW, CellState.values()[i], CellState.values()[j], CellState.values()[k], Move.values()[0]);
                        game.changeStrategy(BeetleType.HIGH, CellState.values()[i], CellState.values()[j], CellState.values()[k], Move.values()[0]);
                    }
                }
            }
        } else
        {
            // no strategy change
        }

    }

}
