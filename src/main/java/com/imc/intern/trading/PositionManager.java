package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.api.Symbol;

import java.util.HashMap;

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
        if (real)
        {
            if (realPositions.containsKey(sym))
            {
                
            }
            else
            {

            }
        }
        else
        {

        }
    }

}
