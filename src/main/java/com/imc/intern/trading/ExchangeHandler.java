package com.imc.intern.trading;

import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.*;
import com.imc.intern.exchange.datamodel.api.Error;
import com.imc.intern.exchange.datamodel.jms.ExposureUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangeHandler implements OrderBookHandler
{
    private TradeEngine trader;
    private BookDepth myBook = new BookDepth();
    private ArbitrageEngine arbitrageMasterRef;

    private int position;
    private double lastPriceAtPosition;

//    private boolean updated;

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public ExchangeHandler(RemoteExchangeView rev, Symbol sym)
    {
        trader = new TradeEngine(rev, sym);
        arbitrageMasterRef = null;
//        updated = false;
        position = 0;
        lastPriceAtPosition = 0;
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

    public void setArbitrageMasterRef(ArbitrageEngine arbitrageMasterRef)
    {
        this.arbitrageMasterRef = arbitrageMasterRef;
    }

    public void handleRetailState(RetailState retailState)
    {
        //Log the retain state
        LOGGER.info(retailState.toString());

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

        if (trade.getSide() == Side.SELL)
        {
            position -= trade.getVolume();
            lastPriceAtPosition = trade.getPrice();
        }
        else
        {
            position += trade.getVolume();
            lastPriceAtPosition = trade.getPrice();
        }

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
