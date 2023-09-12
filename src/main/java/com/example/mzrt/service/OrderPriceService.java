package com.example.mzrt.service;

import com.example.mzrt.enums.AlertMessage;
import com.example.mzrt.model.Deal;

import static com.example.mzrt.enums.AlertMessage.*;
import static com.example.mzrt.enums.Side.isShort;

public class OrderPriceService {

    /**
     * This method is responsible for determining is the received alert
     * have to be sent
     *
     * @param deal      - The deal within which will search sent orders
     * @param alertName - Current entry alert name
     * @return True if the received alert is redundant, false if not
     */
    public static boolean isRedundant(Deal deal, String alertName) {
        AlertMessage alert = valueByName(alertName);

        if (alert.isEntry()) {
            return currentEntryIsPresent(deal, alert)
                    || higherEntryIsPresent(deal, alert)
                    || takeProfitIsPresent(deal);
        }
        return false;
    }

    /**
     * This method is checkin is any take profit already taken
     *
     * @param deal - The deal to be processed
     * @return True if any TP already taken, false if not
     */
    private static boolean takeProfitIsPresent(Deal deal) {
        return deal.getTakePrice1()
                + deal.getTakePrice2()
                + deal.getTakePrice3()
                + deal.getTakePrice4()
                + deal.getTakePrice5() > 0;
    }

    /**
     * This method is responsible for determining is current ENTRY alert
     * is already present in the received deal
     *
     * @param deal  - The deal within which will search sent orders
     * @param alert - Current entry alert
     * @return - True if it's already sent, false if not
     */
    private static boolean currentEntryIsPresent(Deal deal, AlertMessage alert) {
        return switch (alert.getNumber()) {
            case 1 -> deal.getFirstPrice() > 0;
            case 2 -> deal.getSecondPrice() > 0;
            case 3 -> deal.getThirdPrice() > 0;
            case 4 -> deal.getFourthPrice() > 0;
            case 5 -> deal.getFifthPrice() > 0;
            default -> true;
        };
    }

    /**
     * This method is responsible for searching is the ENTRY alert with the higher
     * number was sent before
     *
     * @param deal  - The deal within which will search sent orders
     * @param alert - Current entry alert
     * @return - True if order with the higher alert number was sent before, false if not
     */
    private static boolean higherEntryIsPresent(Deal deal, AlertMessage alert) {
        return switch (alert.getNumber()) {
            case 1 -> (deal.getSecondPrice() + deal.getThirdPrice() + deal.getFourthPrice() + deal.getFifthPrice()) > 0;
            case 2 -> (deal.getThirdPrice() + deal.getFourthPrice() + deal.getFifthPrice()) > 0;
            case 3 -> (deal.getFourthPrice() + deal.getFifthPrice()) > 0;
            case 4 -> deal.getFifthPrice() > 0;
            default -> false;
        };
    }

    /**
     * This method is responsible for getting next free take profit
     *
     * @param deal - The deal within which will searching free TP
     * @return - Alert message
     */
    public static AlertMessage getNextTP(Deal deal) {
        String side = deal.getSide();

        if (deal.getTakePrice4() > 0) {
            return isShort(side) ? STP5 : LTP5;
        } else if (deal.getTakePrice3() > 0) {
            return isShort(side) ? STP4 : LTP4;
        } else if (deal.getTakePrice2() > 0) {
            return isShort(side) ? STP3 : LTP3;
        } else if (deal.getTakePrice1() > 0) {
            return isShort(side) ? STP2 : LTP2;
        } else {
            return isShort(side) ? STP1 : LTP1;
        }
    }

}
