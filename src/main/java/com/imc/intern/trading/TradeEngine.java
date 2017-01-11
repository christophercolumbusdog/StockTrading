package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.Symbol;
import com.imc.intern.exchange.views.ExchangeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TradeEngine
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Map<Long, Order> myBids = new HashMap<>();
    private Map<Long, Order> myAsks = new HashMap<>();
    private ExchangeView exchangeView;
    private Symbol book;
    private int position;

    private double TARGET_VALUE = 20;
    private double OFFSET = 0.1;

    public TradeEngine(ExchangeView rev, Symbol s)
    {
        exchangeView = rev;
        book = s;
        position = 0;
    }

    public void completedBid(long id)
    {
        myBids.remove(id);
    }

    public void completedAsk(long id)
    {
        myAsks.remove(id);
    }

    public void updatePosition(int change)
    {
        position += change;
    }

    public int getPosition()
    {
        return position;
    }

    public void bookChangeHitter(BookDepth activeBook)
    {
        double lowestSellPrice = Double.MAX_VALUE;
        double highestBuyPrice = 0;
        int sellVol = 0;
        int buyVol = 0;

        TreeMap<Double, Integer> bids = activeBook.getBookBids();
        TreeMap<Double, Integer> asks = activeBook.getBookAsks();

        //cproctor: is it possible that there are more opportunities that are profitable? This only looks at the first
        //level of the book
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

        LOGGER.info("Highest Buy = " + highestBuyPrice);
        LOGGER.info("Buy Vol = " + buyVol);
        LOGGER.info("Lowest Sell = " + lowestSellPrice);
        LOGGER.info("Sell Vol = " + sellVol);

        //Check immediate selling opportunity
        checkForSellOp(highestBuyPrice, buyVol, TARGET_VALUE + OFFSET);

        //Check immediate buying opportunity
        checkForBuyOp(lowestSellPrice, sellVol, TARGET_VALUE - OFFSET);

    }

    public void immediateBuyAttempt(BookDepth activeBook, int quantity)
    {
        exchangeView.createOrder(book, activeBook.getLowestAsk(), quantity, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
    }

    public void immediateSellAttempt(BookDepth activeBook, int quantity)
    {
        exchangeView.createOrder(book, activeBook.getHighestBid(), quantity, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
    }

    private void checkForBuyOp(double lowestSellPrice, int sellVol, double threshold)
    {
        if (sellVol > 0 && lowestSellPrice < threshold)
        {
            LOGGER.info("SUBMITTING BID");
            exchangeView.createOrder(book, lowestSellPrice, sellVol, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
        }
    }

    private void checkForSellOp(double highestBuyPrice, int buyVol, double threshold)
    {
        if (buyVol > 0 && highestBuyPrice > threshold)
        {
            LOGGER.info("SUBMITTING ASK");
            exchangeView.createOrder(book, highestBuyPrice, buyVol, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
        }
    }


}
