package com.imc.intern.trading;

import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OwnTrade;
import com.imc.intern.exchange.datamodel.api.Symbol;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ExchangeHandlerTest
{
    @Before
    public void setUp() throws Exception
    {

    }

    @Test
    public void tradeHandlerBalances()
    {
        OwnTrade myTrade = Mockito.mock(OwnTrade.class);
        ArbitrageEngine engine = Mockito.mock(ArbitrageEngine.class);
        RemoteExchangeView remote = Mockito.mock(RemoteExchangeView.class);
        Symbol sym = Symbol.of("CCY1");

        ExchangeHandler handler = new ExchangeHandler(remote, sym);


        handler.setArbitrageMasterRef(engine);

        Mockito.when(engine.getIdealPosition(handler)).thenReturn(10);
        Mockito.when(myTrade.getPrice()).thenReturn(6.0);
        Mockito.when(myTrade.getVolume()).thenReturn(6);
        Mockito.when(myTrade.getSide()).thenReturn(Side.BUY);

        handler.handleOwnTrade(myTrade);


    }
}