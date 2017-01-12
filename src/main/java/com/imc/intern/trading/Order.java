package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.Symbol;

public class Order
{
    // NAJ: Usually, classes have private members. I would make these fields private and generate getters for them all.
    // NAJ: IntelliJ can generate these for you easier.
    private long id;
    private Symbol book;
    private int volume;
    private double price;
    private OrderType orderType;
    private Side side;

    public Order(long i, Symbol sm, double prc, int vol, OrderType order, Side sd) {
        id = i;
        book = sm;
        volume = vol;
        price = prc;
        orderType = order;
        side = sd;
    }

    public long getId()
    {
        return id;
    }

    public Symbol getBook()
    {
        return book;
    }

    public int getVolume()
    {
        return volume;
    }

    public double getPrice()
    {
        return price;
    }

    public OrderType getOrderType()
    {
        return orderType;
    }

    public Side getSide()
    {
        return side;
    }
}
