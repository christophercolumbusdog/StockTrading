package com.imc.intern.trading;

import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.Symbol;
import com.imc.intern.exchange.views.ExchangeView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by imc on 10/01/2017.
 */
public class TradeEngineTest
{
    //cproctor: A lot of your tests are setting up stuff. We can pull this into a setup method like so:
//    private ExchangeView exchangeView;
//    private Symbol book;
//    private TradeEngine victim;
//    @Before
//    public void setUp() {
//        exchangeView = Mockito.mock(ExchangeView.class);
//        book = Symbol.of("CCY1");
//        victim = new TradeEngine(exchangeView, book);
//    }
    //@Before methods are called before each test.

    @Test
    public void shouldSellWithOpportunity() throws Exception
    {
        int price = 30, volume = 100;

        RemoteExchangeView remote = Mockito.mock(RemoteExchangeView.class);
        Symbol book = Symbol.of("CCY1");
        TradeEngine engine = new TradeEngine(remote, book);

        BookDepth bd = new BookDepth();
        //cproctor: Here we should really be using consumeRetailState
        bd.refreshBook(price, volume, Side.BUY);

        engine.retailUpdateHitterV2(bd);

        Mockito.verify(remote).createOrder(book, price, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
    }

    @Test
    public void shouldNotSellWithOpportunity() throws Exception
    {
        double price = 20.1;
        int volume = 100;

        //cproctor: Throughout your code you can use ExchangeView instead of RemoteExchangeView
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
        //cproctor: One of these types is slightly incorrect :)
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