package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.*;
import com.imc.intern.exchange.datamodel.api.Error;
import com.imc.intern.exchange.datamodel.jms.ExposureUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ExchangeHandler implements OrderBookHandler
{
    private TradeEngine trader;
    private BookDepth myBook;
    private ArbitrageEngine arbitrageMasterRef;
    private HashMap<Long, Integer> outstandingOrders;
    private int balanceAttempts;

    private int currentTradesSinceTime;

    private int position;
    private double lastTradedPrice;

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public ExchangeHandler(TradeEngine trader, BookDepth myBook)
    {
        this.trader = trader;
        arbitrageMasterRef = null;
        position = 0;
        outstandingOrders = new HashMap<>();
        lastTradedPrice = 0;
        currentTradesSinceTime = 0;
        balanceAttempts = 0;
        this.myBook = myBook;
    }

    public double getLastTradedPrice()
    {
        return lastTradedPrice;
    }

    public BookDepth getMyBook()
    {
        return myBook;
    }

    public TradeEngine getTrader()
    {
        return trader;
    }

    public int getPosition()
    {
        return position;
    }

    public boolean hasOutstandingOrders()
    {
        return outstandingOrders.size() > 0;
    }

    public void setArbitrageMasterRef(ArbitrageEngine arbitrageMasterRef)
    {
        this.arbitrageMasterRef = arbitrageMasterRef;
    }

    public void handleRetailState(RetailState retailState)
    {
        //Log the retain state
        LOGGER.info(retailState.toString());

        if (outstandingOrders.size() > 0)
        {
            LOGGER.info("PENDING TRADE FOR " + trader.getSymbol());
            balanceAttempts++;

            if (balanceAttempts > 50)
            {
                LOGGER.info("PENDING TRADES FAILED, ATTEMPT TO FORCE BALANCE.............");
                forceBalance();
            }
        }
        else
        {
            balanceAttempts = 0;
        }


        //Refreshes the book based on the new retailState
        myBook.consumeRetailState(retailState);

        //Ensures a valid last traded from for the first trade
        if (lastTradedPrice < 0.000001 && lastTradedPrice > -0.000001)
        {
            LOGGER.info("SETTING UP LAST TRADED PRICE FOR " + trader.getSymbol());

            //try to get a good market price, based on the median of the spread
            lastTradedPrice = (myBook.getLowestAsk() + myBook.getHighestBid()) / ((double)(2));
        }

        arbitrageMasterRef.checkArbitrage();
    }

    //EXPERIMENTAL
    public void forceBalance()
    {
        balanceAttempts = 0;

        HashMap<Long, Integer> newOutstanding = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : outstandingOrders.entrySet())
        {
            trader.cancelTrade(entry.getKey()); //WHAT IF CANCEL DOES NOT EXECUTE IN TIME?

            long orderID;

            if (entry.getValue() > 0)
            {
                LOGGER.info("BALANCING BY BUYING " + entry.getValue());
                orderID = trader.submitGTCBuyOrder(myBook.getLowestAsk(), entry.getValue());
            }
            else
            {
                LOGGER.info("BALANCING BY SELLING " + (entry.getValue() * -1));
                orderID = trader.submitGTCSellOrder(myBook.getHighestBid(), entry.getValue());
            }

            newOutstanding.put(orderID, entry.getValue());
        }

        outstandingOrders = newOutstanding;
    }

    //Called when anything occurs with MY trades are updated
    public void handleExposures(ExposureUpdate exposures)
    {
        LOGGER.info("Exposure handled, " + exposures.toString());
    }

    //Called when my trade closes
    public void handleOwnTrade(OwnTrade trade)
    {
        LOGGER.info("ORDER EXECUTED!!! " + trade.toString());

        lastTradedPrice = trade.getPrice();

        if (outstandingOrders.containsKey(trade.getOrderId()))
        {
            int position = outstandingOrders.get(trade.getOrderId());

            if (trade.getSide() == Side.BUY)
            {
                position -= trade.getVolume();
            }
            else
            {
                position += trade.getVolume();
            }

            if (position == 0)
                outstandingOrders.remove(trade.getOrderId());
            else
                outstandingOrders.put(trade.getOrderId(), position);

            return;
        }

        int ideal = arbitrageMasterRef.getIdealPosition(this);

        LOGGER.info("IDEAL POSITION " + ideal);
        LOGGER.info("TRADE OCCURRED FOR " + trade.getVolume());

        if (trade.getSide() == Side.SELL)
        {
            position -= trade.getVolume();
        }
        else
        {
            position += trade.getVolume();
        }

        int needed = ideal - position;

        LOGGER.info("CURRENT POSITION AFTER TRADE " + position);

        if (needed != 0)
        {
            LOGGER.info("TRADE INCOMPLETE, SENDING GTC ORDER...");
            sendBalancingTrades(needed, trade.getPrice(), ideal);
        }

        LOGGER.info("NEW POSITION " + position);
    }

    public void sendBalancingTrades(int needed, double basePrice, int ideal)
    {
        long orderID;

        if (System.currentTimeMillis() % 31000 == 0)
        {
            currentTradesSinceTime = 0;
        }

        if (currentTradesSinceTime >= 12)
            return;

        if (needed > 0)
        {
            LOGGER.info("BALANCING BY BUYING " + needed);
            orderID = trader.submitGTCBuyOrder(basePrice + .00, needed); //.00 seems to work, was .05
        }
        else
        {
            LOGGER.info("BALANCING BY SELLING " + (needed * -1));
            orderID = trader.submitGTCSellOrder(basePrice - .00, needed * -1); //.00 seems to work, was .05
        }

        position = ideal;
        ++currentTradesSinceTime;



        outstandingOrders.put(orderID, needed);
    }

    //Called when any trade closes
    public void handleTrade(Trade trade)
    {
        LOGGER.info("Trade occurred, " + trade.toString());
    }

    //Handles any errors
    public void handleError(Error error)
    {
        LOGGER.info("There as an ERROR, " + error.toString());
    }
}
