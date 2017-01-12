package com.imc.intern.trading;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArbitrageEngine
{
    private ExchangeHandler exMain;
    private ExchangeHandler exDerivative1;
    private ExchangeHandler exDerivative2;

    private int tacoIdealPos, beefIdealPos, tortIdealPos;
    private long lastTrade;

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public ArbitrageEngine(ExchangeHandler e1, ExchangeHandler e2, ExchangeHandler e3)
    {
        exMain = e1;
        exDerivative1 = e2;
        exDerivative2 = e3;
        tacoIdealPos = beefIdealPos = tortIdealPos = 0;
        lastTrade = 0;
    }

    public enum Action
    {
        NONE, TACO_TO_PARTS, PARTS_TO_TACO
    }

    //CHANGE TO PUBLIC FOR TEST, TO CHANGE BACK TO PRIVATE
    @VisibleForTesting
    public Action calculateArbitrageOpportunity(BookDepth whole, BookDepth half1, BookDepth half2)
    {
        // NAJ: as we spoke, we can move this out to a strategy pattern: https://en.wikipedia.org/wiki/Strategy_pattern
        boolean tacoToParts = false, partsToTaco = false;

        double wholeAskPrice = whole.getLowestAsk();
        double half1AskPrice = half1.getLowestAsk();
        double half2AskPrice = half2.getLowestAsk();

        double wholeBidPrice = whole.getHighestBid();
        double half1BidPrice = half1.getHighestBid();
        double half2BidPrice = half2.getHighestBid();

        LOGGER.info("BEST TACO ASK: " + wholeAskPrice);
        LOGGER.info("BEST BEEF BID: " + half1BidPrice);
        LOGGER.info("BEST TORT BID: " + half2BidPrice);
        LOGGER.info(">>>>>>>>>>>>>>>");
        LOGGER.info("BEST TACO BID: " + wholeBidPrice);
        LOGGER.info("BEST BEEF ASK: " + half1AskPrice);
        LOGGER.info("BEST TORT ASK: " + half2AskPrice);


        //SHOULD ADD OFFSETS TO ENSURE SAFETY IN TRADE
        if (wholeBidPrice > half1AskPrice + half2AskPrice)
        {
            partsToTaco = true;
        }
        else if (wholeAskPrice < half1BidPrice + half2BidPrice)
        {
            tacoToParts = true;
        }

        if (tacoToParts){
            LOGGER.info("TACO TO PARTS");
            return Action.TACO_TO_PARTS;
        }
        else if (partsToTaco)
        {
            LOGGER.info("PARTS TO TACO");
            return Action.PARTS_TO_TACO;
        }
        else
        {
            LOGGER.info("NONE");
            return Action.NONE;
        }

    }

    public void checkArbitrage()
    {
        if (System.currentTimeMillis() - lastTrade < 30000)
            return;
        if (hasCriticalImbalance())
            return;
        if (exMain.hasOutstandingOrders() || exDerivative1.hasOutstandingOrders() || exDerivative2.hasOutstandingOrders())
            return;

        Action decision = calculateArbitrageOpportunity(exMain.getMyBook(), exDerivative1.getMyBook(), exDerivative2.getMyBook());

        int tacoBidVol = exMain.getMyBook().getBidVolume();
        int tacoAskVol = exMain.getMyBook().getAskVolume();

        int beefBidVol = exDerivative1.getMyBook().getBidVolume();
        int beefAskVol = exDerivative1.getMyBook().getAskVolume();

        int tortBidVol = exDerivative2.getMyBook().getBidVolume();
        int tortAskVol = exDerivative2.getMyBook().getAskVolume();

        if (decision == Action.TACO_TO_PARTS)
        {
            int quantity = findMinimumQuantity(tacoAskVol, beefBidVol, tortBidVol);
            LOGGER.info("SENDING BID TRADE FOR " + quantity);

            exMain.getTrader().immediateBuyAttempt(exMain.getMyBook(), quantity);
            exDerivative1.getTrader().immediateSellAttempt(exDerivative1.getMyBook(), quantity);
            exDerivative2.getTrader().immediateSellAttempt(exDerivative2.getMyBook(), quantity);

            tacoIdealPos += quantity;
            beefIdealPos -= quantity;
            tortIdealPos -= quantity;

            lastTrade = System.currentTimeMillis();
        }
        else if (decision == Action.PARTS_TO_TACO)
        {
            int quantity = findMinimumQuantity(tacoBidVol, beefAskVol, tortAskVol);
            LOGGER.info("SENDING ASK TRADE FOR " + quantity);

            exMain.getTrader().immediateSellAttempt(exMain.getMyBook(), quantity);
            exDerivative1.getTrader().immediateBuyAttempt(exMain.getMyBook(), quantity);
            exDerivative2.getTrader().immediateBuyAttempt(exMain.getMyBook(), quantity);

            tacoIdealPos -= quantity;
            beefIdealPos += quantity;
            tortIdealPos += quantity;

            lastTrade = System.currentTimeMillis();
        }

    }

    public boolean hasCriticalImbalance()
    {
        boolean imbalance = false;

        if (Math.abs(tacoIdealPos - exMain.getPosition()) > 2)
        {
            imbalance = true;

            if (!exMain.hasOutstandingOrders())
            {
                int needed = tacoIdealPos - exMain.getPosition();
                double targetPrice = exMain.getLastTradedPrice();

                exMain.sendBalancingTrades(needed, targetPrice, tacoIdealPos);
            }
        }
        else if (Math.abs(beefIdealPos - exDerivative1.getPosition()) > 2)
        {
            imbalance = true;

            if (!exDerivative1.hasOutstandingOrders())
            {
                int needed = beefIdealPos - exDerivative1.getPosition();
                double targetPrice = exDerivative1.getLastTradedPrice();

                exDerivative1.sendBalancingTrades(needed, targetPrice, beefIdealPos);
            }
        }
        else if (Math.abs(tortIdealPos - exDerivative2.getPosition()) > 2)
        {
            imbalance = true;

            if (!exDerivative2.hasOutstandingOrders())
            {
                int needed = tortIdealPos - exDerivative2.getPosition();
                double targetPrice = exDerivative2.getLastTradedPrice();

                exDerivative2.sendBalancingTrades(needed, targetPrice, tortIdealPos);
            }
        }

        return imbalance;
    }

    public int getIdealPosition(ExchangeHandler e)
    {
        if (e == exMain)
        {
            return tacoIdealPos;
        }
        else if (e == exDerivative1)
        {
            return beefIdealPos;
        }
        else if (e == exDerivative2)
        {
            return tortIdealPos;
        }
        else
        {
            LOGGER.info("NON EXISTENT EXCHANGE HANDLER!!! WHAT???");
            return Integer.MAX_VALUE;
        }
    }


    public int findMinimumQuantity(int a, int b, int c)
    {
        return Math.min(100, Math.min(a, Math.min(b, c)));
    }

}
