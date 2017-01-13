package com.imc.intern.trading;

import com.imc.intern.exchange.client.ExchangeClient;
import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.api.*;

public class Main
{
    private static final String EXCHANGE_URL = "tcp://54.227.125.23:61616";
    private static final String USERNAME = "ccygnus";
    private static final String PASSWORD = "height apartment tonight grain";
    //private static final String BOOK = "CCY1";

    private static final String TACO = "TACO";
    private static final String TORT = "TORT";
    private static final String BEEF = "BEEF";

    /*
        ** Overall, looks like you're still in the process of refactoring your code. I'd continue this work. Obviously
        * focus on functionality first, but early refactor can make later functionality easier.
     */
    // mwang: Pretty much what we discussed yesterday. Functionally, this looks pretty good, but the ExchangeHandler
    // especially is taking care of a lot by itself. YOu should consider pulling dependencies out of ExchangeHandler
    // to their own classes.
    public static void main(String[] args) throws Exception
    {
        Symbol taco = Symbol.of(TACO);
        Symbol beef = Symbol.of(BEEF);
        Symbol tort = Symbol.of(TORT);

        ExchangeClient client = ExchangeClient.create(EXCHANGE_URL, Account.of(USERNAME), PASSWORD);
        client.start();

        RemoteExchangeView remote = client.getExchangeView();

        BookDepth tacoBook = new BookDepth();
        BookDepth beefBook = new BookDepth();
        BookDepth tortBook = new BookDepth();

        ExchangeHandler tacoHandler = new ExchangeHandler(new TradeEngine(remote, taco), tacoBook);
        ExchangeHandler beefHandler = new ExchangeHandler(new TradeEngine(remote, beef), beefBook);
        ExchangeHandler tortHandler = new ExchangeHandler(new TradeEngine(remote, tort), tortBook);

        ArbitrageEngine arbitrageEngine = new ArbitrageEngine(tacoHandler, beefHandler, tortHandler);

        tacoHandler.setArbitrageMasterRef(arbitrageEngine);
        beefHandler.setArbitrageMasterRef(arbitrageEngine);
        tortHandler.setArbitrageMasterRef(arbitrageEngine);

        remote.subscribe(taco, tacoHandler);
        remote.subscribe(beef, beefHandler);
        remote.subscribe(tort, tortHandler);
    }


}
