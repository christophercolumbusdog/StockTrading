package com.imc.intern.trading;

import com.imc.intern.exchange.client.ExchangeClient;
import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.*;
import com.imc.intern.exchange.datamodel.api.Error;
import com.imc.intern.exchange.datamodel.jms.ExposureUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main
{
    private static final String EXCHANGE_URL = "tcp://wintern.imc.com:61616";
    private static final String USERNAME = "ccygnus";
    private static final String PASSWORD = "height apartment tonight grain";
    private static final String BOOK = "CCY1";

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws Exception
    {
        ExchangeClient client = ExchangeClient.create(EXCHANGE_URL, Account.of(USERNAME), PASSWORD);
        RemoteExchangeView remote = client.getExchangeView();
        Map<Long, Order> myOrders = new HashMap<Long, Order>();

        remote.subscribe(Symbol.of(BOOK), new OrderBookHandler() {
            //Called when the depth changes at all, or every 10 seconds

            private TradeEngine trader = new TradeEngine(remote, Symbol.of(BOOK));
            private BookDepth myBook = new BookDepth();

            public void handleRetailState(RetailState retailState)
            {
                System.out.println(retailState);

                //Refreshes the book based on the new retailState
                myBook.consumeRetailState(retailState);

//                trader.retailUpdateHitter(retailState);
                trader.retailUpdateHitterV2(myBook);
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

        });

        client.start();
    }


}
