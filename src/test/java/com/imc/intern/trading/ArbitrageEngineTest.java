package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.api.RetailState;
import com.imc.intern.exchange.datamodel.api.Symbol;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class ArbitrageEngineTest
{

    private BookDepth taco;
    private BookDepth beef;
    private BookDepth tort;
    private RetailState rsBeef;
    private RetailState rsTaco;
    private RetailState rsTort;

    private Symbol symTaco, symBeef, symTort;

    private List<RetailState.Level> tacoBids;
    private List<RetailState.Level> tacoAsks;
    private List<RetailState.Level> beefBids;
    private List<RetailState.Level> beefAsks;
    private List<RetailState.Level> tortBids;
    private List<RetailState.Level> tortAsks;

    private long time;

    private ArbitrageEngine victim;

    private ExchangeHandler ex1, ex2, ex3;

    @Before
    public void setUp() throws Exception
    {
        taco = new BookDepth();
        beef = new BookDepth();
        tort = new BookDepth();

        symTaco = Symbol.of("TACO");
        symBeef = Symbol.of("BEEF");
        symTort = Symbol.of("TORT");

        tacoBids = new ArrayList<>();
        tacoAsks = new ArrayList<>();
        beefBids = new ArrayList<>();
        beefAsks = new ArrayList<>();
        tortBids = new ArrayList<>();
        tortAsks = new ArrayList<>();

        time = 123456;
    }

    @Test
    public void correctlyCalculateTacoToParts() throws Exception
    {
        tacoAsks.add(new RetailState.Level(10.0, 100));

        beefBids.add(new RetailState.Level(8.0, 100));
        tortBids.add(new RetailState.Level(8.0, 100));

        completeArbitrage();
    }

    @Test
    public void correctlyCalculatePartsToTaco() throws Exception
    {
        tacoBids.add(new RetailState.Level(10.0, 100));

        beefAsks.add(new RetailState.Level(4.0, 100));
        tortAsks.add(new RetailState.Level(4.0, 100));

        completeArbitrage();
    }

    @Test
    public void correctlyCalculateNoAction1() throws Exception
    {
        tacoBids.add(new RetailState.Level(10.0, 100));

        beefAsks.add(new RetailState.Level(5.0, 100));
        tortAsks.add(new RetailState.Level(5.0, 100));

        completeArbitrage();
    }

    @Test
    public void correctlyCalculateNoAction2() throws Exception
    {
        tacoAsks.add(new RetailState.Level(10.0, 100));

        beefBids.add(new RetailState.Level(5.0, 100));
        tortBids.add(new RetailState.Level(5.0, 100));

        completeArbitrage();
    }

    public void completeArbitrage()
    {
        rsTaco = new RetailState(symTaco, tacoBids, tacoAsks, time);
        rsBeef = new RetailState(symBeef, beefBids, beefAsks, time);
        rsTort = new RetailState(symTort, tortBids, tortAsks, time);

        taco.consumeRetailState(rsTaco);
        beef.consumeRetailState(rsBeef);
        tort.consumeRetailState(rsTort);

        ex1 = Mockito.mock(ExchangeHandler.class);
        ex2 = Mockito.mock(ExchangeHandler.class);
        ex3 = Mockito.mock(ExchangeHandler.class);

        victim = new ArbitrageEngine(ex1, ex2, ex3);

        victim.calculateArbitrageOpportunity(taco, beef, tort);
    }
}