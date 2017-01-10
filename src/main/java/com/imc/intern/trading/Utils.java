package com.imc.intern.trading;

/**
 * Created by imc on 10/01/2017.
 */
public class Utils
{
    public void calculateArbitrageOpportunity()
    {

    }
}





//        //Get any current bid orders
//        for (Map.Entry<Long, Order> currOrders : myBids.entrySet())
//        {
//            System.out.println("FOUND EXISTING BUY");
//            buying = currOrders.getValue();
//        }
//
//        //Get any current ask orders
//        for (Map.Entry<Long, Order> currOrders : myAsks.entrySet())
//        {
//            System.out.println("FOUND EXISTING SELL");
//            selling = currOrders.getValue();
//        }

////Iterate through the bids
//for (RetailState.Level level : rs.getBids())
//        {
//        if (level.getPrice() > highestBuyPrice)
//        {
//        highestBuyPrice = level.getPrice();
//        buyVol = level.getVolume();
//        }
//        }
//
//        System.out.println("Highest Buy = " + highestBuyPrice);
//        System.out.println("Buy Vol = " + buyVol);
//
//        //Iterate through the sells
//        for (RetailState.Level level : rs.getAsks())
//        {
//        if (level.getPrice() < lowestSellPrice)
//        {
//        lowestSellPrice = level.getPrice();
//        sellVol = level.getVolume();
//        }
//        }
//
//        System.out.println("Lowest Sell = " + lowestSellPrice);
//        System.out.println("Sell Vol = " + sellVol);
