package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.RetailState;
import java.util.TreeMap;

public class BookDepth
{
    private TreeMap<Double, Integer> bids = new TreeMap<>();
    private TreeMap<Double, Integer> asks = new TreeMap<>();


    public BookDepth() {} // NAJ: default constructors are implied in java, don't need this

    public TreeMap<Double, Integer> getBookBids()
    {
        return bids;
    } // since trade engine could be deleted, these methods are removed too. Nice clean up.

    public TreeMap<Double, Integer> getBookAsks()
    {
        return asks;
    }

    public double getHighestBid()
    {
        return (bids.size() > 0)? bids.lastEntry().getKey() : 0;
    }

    public int getBidVolume()
    {
        return (bids.size() > 0)? bids.lastEntry().getValue() : 0;
    }

    public double getLowestAsk()
    {
        return (asks.size() > 0)? asks.firstEntry().getKey() : Double.MAX_VALUE;
    }

    public int getAskVolume()
    {
        return (asks.size() > 0)? asks.firstEntry().getValue() : 0;
    }

    private void refreshBook(double price, int volume, Side s) // NAJ: would use side instead of "s", more descriptive
    {
        TreeMap<Double, Integer> tree; // NAJ: you can define and assign tree in the same line.

        tree = (s == Side.BUY) ? bids : asks;

        if (tree.containsKey(price) && volume == 0)
        {
            tree.remove(price);
        }
        else // NAJ: else if in this scenario
        {
            if (volume != 0)
                tree.put(price, volume);
        }
    }

    public void consumeRetailState(RetailState retailState)
    {
        for (RetailState.Level level : retailState.getBids())
        {
            refreshBook(level.getPrice(), level.getVolume(), Side.BUY);
        }

        for (RetailState.Level level : retailState.getAsks())
        {
            refreshBook(level.getPrice(), level.getVolume(), Side.SELL);
        }
    }

    public void subtractVolume(double price, int volume, Side s)
    {
        TreeMap<Double, Integer> tree;

        tree = (s == Side.BUY) ? bids : asks;

        int currentVolume = 0;

        if (tree.containsKey(price))
        {
            currentVolume = tree.get(price) - volume;
            if (currentVolume <= 0)
            {
                tree.remove(price);
            }
            else
                tree.put(price, currentVolume);
        }
    }


}