package com.imc.intern.trading;

/**
 * Created by imc on 10/01/2017.
 */
public class Utils
{
    public void calculateArbitrageOpportunity(BookDepth whole, BookDepth half1, BookDepth half2)
    {
        boolean tacoToParts = false, partsToTaco = false;

        double wholeAskPrice = whole.getLowestAsk();
        double half1AskPrice = half1.getLowestAsk();
        double half2AskPrice = half2.getLowestAsk();

        double wholeBidPrice = whole.getHighestBid();
        double half1BidPrice = half1.getHighestBid();
        double half2BidPrice = half2.getHighestBid();

        if (wholeBidPrice > half1AskPrice + half2AskPrice)
        {
            partsToTaco = true;
        }
        else if (wholeAskPrice < half1BidPrice + half2BidPrice)
        {
            tacoToParts = true;
        }


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
