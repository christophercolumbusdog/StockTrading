package com.imc.intern.trading;

import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.Symbol;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by imc on 10/01/2017.
 */
public class TradeEngineTest
{
    @Test
    public void shouldSellWithOpportunity() throws Exception
    {
        int price = 30, volume = 100;

        RemoteExchangeView remote = Mockito.mock(RemoteExchangeView.class);
        Symbol book = Symbol.of("CCY1");
        TradeEngine engine = new TradeEngine(remote, book);

        BookDepth bd = new BookDepth();
        bd.refreshBook(price, volume, Side.BUY);

        engine.retailUpdateHitterV2(bd);

        Mockito.verify(remote).createOrder(book, price, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
    }

    @Test
    public void shouldNotSellWithOpportunity() throws Exception
    {
        double price = 20.1;
        int volume = 100;

        RemoteExchangeView remote = Mockito.mock(RemoteExchangeView.class);
        Symbol book = Symbol.of("CCY1");
        TradeEngine engine = new TradeEngine(remote, book);

        BookDepth bd = new BookDepth();
        bd.refreshBook(price, volume, Side.BUY);

        engine.retailUpdateHitterV2(bd);

        Mockito.verifyZeroInteractions(remote);
    }

    @Test
    public void shouldBuyWithOpportunity() throws Exception
    {
        int price = 15, volume = 100;

        RemoteExchangeView remote = Mockito.mock(RemoteExchangeView.class);
        Symbol book = Symbol.of("CCY1");
        TradeEngine engine = new TradeEngine(remote, book);

        BookDepth bd = new BookDepth();
        bd.refreshBook(price, volume, Side.SELL);

        engine.retailUpdateHitterV2(bd);

        Mockito.verify(remote).createOrder(book, price, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
    }

    @Test
    public void shouldNotBuyWithOpportunity() throws Exception
    {
        int volume = 100;
        double price = 19.9;

        RemoteExchangeView remote = Mockito.mock(RemoteExchangeView.class);
        Symbol book = Symbol.of("CCY1");
        TradeEngine engine = new TradeEngine(remote, book);

        BookDepth bd = new BookDepth();
        bd.refreshBook(price, volume, Side.SELL);

        engine.retailUpdateHitterV2(bd);

        Mockito.verifyZeroInteractions(remote);
    }
}