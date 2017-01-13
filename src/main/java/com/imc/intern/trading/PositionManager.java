package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.api.Symbol;

import java.util.HashMap;

/*
CLASS IN PROGRESS, MAY NOT BE COMPLETED BY END OF DAY FRIDAY
 */

public class PositionManager
{
    HashMap<Symbol, Integer> realPositions;
    HashMap<Symbol, Integer> idealPositions;

    public PositionManager()
    {
        realPositions = new HashMap<>();
        idealPositions = new HashMap<>();
    }

    public void updatePosition(Symbol sym, int quantity, boolean real)
    {
        HashMap<Symbol, Integer> map;

        if (real)
        {
            map = realPositions;
        }
        else
        {
            map = idealPositions;
        }

        if (map.containsKey(sym))
        {
            map.put(sym, map.get(sym) + quantity);
        }
        else
        {
            map.put(sym, quantity);
        }
    }

    public boolean hasImbalance(Symbol main, Symbol derivative1, Symbol derivative2)
    {
        int idealMain = idealPositions.getOrDefault(main, 0);
        int idealDer1 = idealPositions.getOrDefault(derivative1, 0);
        int idealDer2 = idealPositions.getOrDefault(derivative2, 0);

        int realMain = realPositions.getOrDefault(main, 0);
        int realDer1 = realPositions.getOrDefault(derivative1, 0);
        int realDer2 = realPositions.getOrDefault(derivative2, 0);


        boolean imbalance = false;

        if (Math.abs(idealMain - realMain) > 2)
        {
            imbalance = true;
        }
        else if (Math.abs(idealDer1 - realDer1) > 2)
        {
            imbalance = true;
        }
        else if (Math.abs(idealDer2 - realDer2) > 2)
        {
            imbalance = true;
        }

        return imbalance;
    }
}
