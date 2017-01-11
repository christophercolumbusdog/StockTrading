package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.RetailState;
import com.imc.intern.exchange.datamodel.api.Symbol;
import com.imc.intern.exchange.views.ExchangeView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imc on 10/01/2017.
 */
public class TradeEngineTest
{
    private ExchangeView exchangeView;
    private Symbol book;
    private TradeEngine victim;
    private List<RetailState.Level> bids;
    private List<RetailState.Level> asks;
    private long time;
    private double price;
    private int volume;
    private RetailState rs;
    private BookDepth bd;

    @Before
    public void setUp() {
        exchangeView = Mockito.mock(ExchangeView.class);
        book = Symbol.of("CCY1");
        victim = new TradeEngine(exchangeView, book);
        bids = new ArrayList<>();
        asks = new ArrayList<>();
        time = 123456;
        bd = new BookDepth();
    }

    @Test
    public void shouldSellWithOpportunity() throws Exception
    {
        price = 30.0;
        volume = 100;

        bids.add(new RetailState.Level(price, volume));
        rs = new RetailState(book, bids, asks, time);

        bd.consumeRetailState(rs);

        victim.bookChangeHitter(bd);

//        Mockito.when(exchangeView.createOrder()).thenReturn(1);

        Mockito.verify(exchangeView).createOrder(book, price, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
    }

    @Test
    public void shouldNotSellWithOpportunity() throws Exception
    {
        price = 20.1;
        volume = 100;

        bids.add(new RetailState.Level(price, volume));
        rs = new RetailState(book, bids, asks, time);

        bd.consumeRetailState(rs);

        victim.bookChangeHitter(bd);

        Mockito.verifyZeroInteractions(exchangeView);
    }

    @Test
    public void shouldBuyWithOpportunity() throws Exception
    {
        price = 15.0;
        volume = 50;

        asks.add(new RetailState.Level(price, volume));
        rs = new RetailState(book, bids, asks, time);

        bd.consumeRetailState(rs);

        victim.bookChangeHitter(bd);

        Mockito.verify(exchangeView).createOrder(book, price, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
    }

    @Test
    public void shouldNotBuyWithOpportunity() throws Exception
    {
        price = 19.9;
        volume = 40;

        asks.add(new RetailState.Level(price, volume));
        rs = new RetailState(book, bids, asks, time);

        bd.consumeRetailState(rs);

        victim.bookChangeHitter(bd);

        Mockito.verifyZeroInteractions(exchangeView);
    }
}