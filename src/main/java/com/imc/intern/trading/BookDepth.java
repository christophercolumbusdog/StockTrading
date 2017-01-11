package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.RetailState;
import java.util.TreeMap;

/**
 * Created by imc on 10/01/2017.
 */
public class BookDepth
{
    private TreeMap<Double, Integer> bids = new TreeMap<>();
    private TreeMap<Double, Integer> asks = new TreeMap<>();


    public BookDepth() {}

    public TreeMap<Double, Integer> getBookBids()
    {
        return bids;
    }

    public TreeMap<Double, Integer> getBookAsks()
    {
        return asks;
    }

    public double getHighestBid()
    {
        return (bids.size() > 0)? bids.lastEntry().getKey() : -1;
    }

    public int getBidVolume()
    {
        return (bids.size() > 0)? bids.lastEntry().getValue() : 0;
    }

    public double getLowestAsk()
    {
        return (asks.size() > 0)? asks.firstEntry().getKey() : -1;
    }

    public int getAskVolume()
    {
        return (asks.size() > 0)? asks.firstEntry().getValue() : 0;
    }

    //cproctor: This should be private. I see that it's used in tests but those can be changed to use consumeRetailState
    public void refreshBook(double price, int volume, Side s)
    {
        TreeMap<Double, Integer> tree;

        tree = (s == Side.BUY) ? bids : asks;

        if (tree.containsKey(price) && volume == 0)
        {
            tree.remove(price);
        }
        //cproctor: We can simplify this a little bit as such:
        // else if (volume !=0) {
        //  tree.put(price, volume);
        //}
        else
        {
            if (volume == 0)
                return;
            else
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


}