package com.imc.intern.trading;

import com.imc.intern.exchange.client.ExchangeClient;
import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.Account;
import com.imc.intern.exchange.datamodel.api.OrderBookHandler;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.RetailState;
import com.imc.intern.exchange.datamodel.api.Symbol;

public class Main
{
    private static final String EXCHANGE_URL = "tcp://wintern.imc.com:61616";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";
    private static final String BOOK = "";

    public static void main(String[] args) throws Exception
    {
        ExchangeClient client = ExchangeClient.create(EXCHANGE_URL, Account.of(USERNAME), PASSWORD);
        client.getExchangeView().subscribe(new OrderBookHandler() {
            public void handleRetailState(RetailState retailState) {
                System.out.println(retailState);
            }
        });

        int price = 10;
        int volume = 100;
        client.start();
        client.getExchangeView().createOrder(Symbol.of(BOOK), price, volume, OrderType.GOOD_TIL_CANCEL, Side.BUY);
    }
}
