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

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public ExchangeHandler(RemoteExchangeView rev, Symbol sym)
    {
        trader = new TradeEngine(rev, sym);
        arbitrageMasterRef = null;
    }

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
        System.out.println("Exposure handled, " + exposures.toString());
    }

    //Called when my trade closes
    public void handleOwnTrade(OwnTrade trade)
    {
        if (trade.getSide() == Side.BUY)
        {
            trader.completedBid(trade.getOrderId());
        }
        if (trade.getSide() == Side.SELL)
        {
            trader.completedAsk(trade.getOrderId());
        }
        System.out.println("ORDER EXECUTED!!!");
    }

    //Called when any trade closes
    public void handleTrade(Trade trade)
    {
        System.out.println("Trade occurred, " + trade.toString());
    }

    //Handles any errors
    public void handleError(Error error)
    {
        System.out.println("There as an ERROR, " + error.toString());
    }
}
