package com.imc.intern.trading;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArbitrageEngine
{
    private ExchangeHandler exMain;
    private ExchangeHandler exDerivative1;
    private ExchangeHandler exDerivative2;
    private double offset;

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public ArbitrageEngine(ExchangeHandler e1, ExchangeHandler e2, ExchangeHandler e3)
    {
        exMain = e1;
        exDerivative1 = e2;
        exDerivative2 = e3;
        offset = 0.1;
    }

    public enum Action
    {
        NONE, TACO_TO_PARTS, PARTS_TO_TACO
    }

    //CHANGE TO PUBLIC FOR TEST, TO CHANGE BACK TO PRIVATE
    @VisibleForTesting
    public Action calculateArbitrageOpportunity(BookDepth whole, BookDepth half1, BookDepth half2)
    {
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
//        if (!isAllUpdated())
//        {
//            LOGGER.info("WAITING FOR BOOK UPDATE");
//            return;
//        }

        Action decision = calculateArbitrageOpportunity(exMain.getMyBook(), exDerivative1.getMyBook(), exDerivative2.getMyBook());

        if (decision == Action.TACO_TO_PARTS)
        {
//            falsifyUpdatedStatus();
            exMain.getTrader().immediateBuyAttempt(exMain.getMyBook(), Math.min(exDerivative1.getMyBook().getBidVolume(), exDerivative2.getMyBook().getBidVolume()));
        }
        else if (decision == Action.PARTS_TO_TACO)
        {
//            falsifyUpdatedStatus();
            exMain.getTrader().immediateSellAttempt(exMain.getMyBook(), Math.min(exDerivative1.getMyBook().getAskVolume(), exDerivative2.getMyBook().getAskVolume()));
        }
    }

//    private void falsifyUpdatedStatus()
//    {
//        exMain.setUpdated(false);
//        exDerivative1.setUpdated(false);
//        exDerivative2.setUpdated(false);
//    }
//
//    private boolean isAllUpdated()
//    {
//        return (exMain.isUpdated() && exDerivative1.isUpdated() && exDerivative2.isUpdated());
//    }
}
