package com.imc.intern.trading;

import com.imc.intern.exchange.client.ExchangeClient;
import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.*;

public class Main
{
    private static final String EXCHANGE_URL = "tcp://54.227.125.23:61616";
    private static final String USERNAME = "ccygnus";
    private static final String PASSWORD = "height apartment tonight grain";
    private static final String BOOK = "CCY1";

    private static final String TACO = "TACO";
    private static final String TORT = "TORT";
    private static final String BEEF = "BEEF";


    public static void main(String[] args) throws Exception
    {
        Symbol taco = Symbol.of(TACO);
        Symbol beef = Symbol.of(BEEF);
        Symbol tort = Symbol.of(TORT);
//        Symbol ccy1 = Symbol.of(BOOK);
        ExchangeClient client = ExchangeClient.create(EXCHANGE_URL, Account.of(USERNAME), PASSWORD);
        client.start();

        RemoteExchangeView remote = client.getExchangeView();

        ExchangeHandler tacoHandler = new ExchangeHandler(remote, taco);
        ExchangeHandler beefHandler = new ExchangeHandler(remote, beef);
        ExchangeHandler tortHandler = new ExchangeHandler(remote, tort);

        ArbitrageEngine arbitrageEngine = new ArbitrageEngine(tacoHandler, beefHandler, tortHandler);

        tacoHandler.setArbitrageMasterRef(arbitrageEngine);
        beefHandler.setArbitrageMasterRef(arbitrageEngine);
        tortHandler.setArbitrageMasterRef(arbitrageEngine);


        //remote.subscribe(ccy1, new ExchangeHandler(remote, ccy1));

        remote.subscribe(taco, tacoHandler);
        remote.subscribe(beef, beefHandler);
        remote.subscribe(tort, tortHandler);

        client.getExchangeView().createOrder(taco, 5000, 200, OrderType.GOOD_TIL_CANCEL, Side.SELL);
    }


}
