package com.imc.intern.trading;

import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.RetailState;
import com.imc.intern.exchange.datamodel.api.Symbol;

import java.util.*;

/**
 * Created by imc on 10/01/2017.
 */
public class TradeEngine
{
    private Map<Long, Order> myBids = new HashMap<Long, Order>();
    private Map<Long, Order> myAsks = new HashMap<Long, Order>();
    private RemoteExchangeView remote;
    private Symbol book;

    private double TARGET_VALUE = 20;
    private double OFFSET = 0.1;

    public TradeEngine(RemoteExchangeView rev, Symbol s)
    {
        remote = rev;
        book = s;
    }

    public void completedBid(long id)
    {
        myBids.remove(id);
    }

    public void completedAsk(long id)
    {
        myAsks.remove(id);
    }


    public void retailUpdateHitterV2(BookDepth activeBook)
    {
        double lowestSellPrice = 100000;
        double highestBuyPrice = 0;
        int sellVol = 0;
        int buyVol = 0;

        TreeMap<Double, Integer> bids = activeBook.getBookBids();
        TreeMap<Double, Integer> asks = activeBook.getBookAsks();

        if (bids.size() > 0)
        {
            highestBuyPrice = bids.lastEntry().getKey();
            buyVol = bids.lastEntry().getValue();
        }

        if (asks.size() > 0)
        {
            lowestSellPrice = asks.firstEntry().getKey();
            sellVol = asks.firstEntry().getValue();
        }

        System.out.println("Highest Buy = " + highestBuyPrice);
        System.out.println("Buy Vol = " + buyVol);
        System.out.println("Lowest Sell = " + lowestSellPrice);
        System.out.println("Sell Vol = " + sellVol);

        //Check immediate selling opportunity
        checkForSellOp(highestBuyPrice, buyVol, TARGET_VALUE + OFFSET);

        //Check immediate buying opportunity
        checkForBuyOp(lowestSellPrice, sellVol, TARGET_VALUE - OFFSET);

    }

    private void checkForBuyOp(double lowestSellPrice, int sellVol, double threshold)
    {
        if (sellVol > 0 && lowestSellPrice < threshold)
        {
            System.out.println("SUBMITTING BID");
            remote.createOrder(book, lowestSellPrice, sellVol, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
        }
    }

    private void checkForSellOp(double highestBuyPrice, int buyVol, double threshold)
    {
        if (buyVol > 0 && highestBuyPrice > threshold)
        {
            System.out.println("SUBMITTING ASK");
            remote.createOrder(book, highestBuyPrice, buyVol, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
        }
    }


}
