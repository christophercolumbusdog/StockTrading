package com.imc.intern.trading;

import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OwnTrade;
import com.imc.intern.exchange.datamodel.api.Symbol;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ExchangeHandlerTest
{
    private OwnTrade myTrade;
    private ArbitrageEngine engine;
    private TradeEngine trader;

    private ExchangeHandler handler;

    @Before
    public void setUp() throws Exception
    {
        myTrade = Mockito.mock(OwnTrade.class);
        engine = Mockito.mock(ArbitrageEngine.class);
        trader = Mockito.mock(TradeEngine.class);

        handler = new ExchangeHandler(trader, new BookDepth());

        handler.setArbitrageMasterRef(engine);
    }

    @Test
    public void tradeHandlerBalances()
    {
        Mockito.when(engine.getIdealPosition(handler)).thenReturn(10);
        Mockito.when(myTrade.getPrice()).thenReturn(6.0);
        Mockito.when(myTrade.getVolume()).thenReturn(6);
        Mockito.when(myTrade.getSide()).thenReturn(Side.BUY);

        handler.handleOwnTrade(myTrade);
    }

    @Test
    public void correctlySendBalancingTradesSell()
    {
        int needed = -3, ideal = 4;
        double price = 23.4;

        handler.sendBalancingTrades(needed, price, ideal);

        Mockito.verify(trader).submitGTCSellOrder(price, needed * -1);
    }

    @Test
    public void correctlySendBalancingTradesBuy()
    {
        int needed = 8, ideal = 4;
        double price = 23.4;

        handler.sendBalancingTrades(needed, price, ideal);

        Mockito.verify(trader).submitGTCBuyOrder(price, needed);
    }
}