package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.Symbol;

/**
 * Created by imc on 10/01/2017.
 */
public class Order
{
    // NAJ: Usually, classes have private members. I would make these fields private and generate getters for them all.
    // NAJ: IntelliJ can generate these for you easier.
    public long id;
    public Symbol book;
    public int volume;
    public double price;
    public OrderType orderType;
    public Side side;

    public Order(long i, Symbol sm, double prc, int vol, OrderType order, Side sd) {
        id = i;
        book = sm;
        volume = vol;
        price = prc;
        orderType = order;
        side = sd;
    }
}
