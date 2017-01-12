package com.imc.intern.trading;

import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.*;
import com.imc.intern.exchange.datamodel.api.Error;
import com.imc.intern.exchange.datamodel.jms.ExposureUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class ExchangeHandler implements OrderBookHandler
{
    // NAJ: You can remove old cold and reference it in the git history, please do so.
    private TradeEngine trader;
    private BookDepth myBook = new BookDepth();
    private ArbitrageEngine arbitrageMasterRef;
    private String sym;
    private HashSet<Long> outstandingOrders;

    private int position;
    private double lastPriceAtPosition;

    private TreeMap<Double, Integer> pendingOrders;

//    private boolean updated;

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public ExchangeHandler(RemoteExchangeView rev, Symbol sym)
    {
        trader = new TradeEngine(rev, sym);
        arbitrageMasterRef = null;
//        updated = false;
        position = 0;
        lastPriceAtPosition = 0;
        pendingOrders = new TreeMap<>();
        outstandingOrders = new HashSet<>();
    }

//    public void setUpdated(boolean updated)
//    {
//        this.updated = updated;
//    }

//    public boolean isUpdated()
//    {
//        return updated;
//    }

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

    public TreeMap<Double, Integer> getPendingOrders()
    {
        return pendingOrders;
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
            LOGGER.info("PENDING TRADE FOR " + sym.toString());
        }

        //Refreshes the book based on the new retailState
        myBook.consumeRetailState(retailState);
//        updated = true;

        /*
            DISABLED STANDARD HITTER, CAN BE RE-ENABLED AS NECESSARY. ARBITRAGE HANDLING TRADE.
         */
        //trader.bookChangeHitter(myBook);

        //Checks arbitrage opportunity
        arbitrageMasterRef.checkArbitrage();
    }

    //Called when anything occurs with MY trades are updated
    public void handleExposures(ExposureUpdate exposures)
    {
        LOGGER.info("Exposure handled, " + exposures.toString());
    }

    //Called when my trade closes
    public void handleOwnTrade(OwnTrade trade)
    {
        LOGGER.info("ORDER EXECUTED!!!");

        if (outstandingOrders.contains(trade.getOrderId()))
        {
            outstandingOrders.remove(trade.getOrderId());
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
            sendBalancingTrades(needed, trade.getPrice(), ideal);
        }

        LOGGER.info("NEW POSITION " + position);

        lastPriceAtPosition = trade.getPrice();
    }

    public void sendBalancingTrades(int needed, double basePrice, int ideal)
    {
        long orderID;
        pendingOrders.put(basePrice, needed);

        //REMOVE, FOR TESTING ONLY
        if (needed > 0)
        {
            LOGGER.info("BALANCING BY BUYING " + needed);
            orderID = trader.submitGTCBuyOrder(basePrice + 1, needed);
            position = ideal;
        }
        else
        {
            LOGGER.info("BALANCING BY SELLING " + (needed * -1));
            orderID = trader.submitGTCSellOrder(basePrice - 1, needed * -1);
            position = ideal;
        }

        outstandingOrders.add(orderID);
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
