package com.example.mzrt.service;

import com.example.mzrt.enums.AlertMessage;
import com.example.mzrt.model.Alert;
import com.example.mzrt.model.Deal;

import static com.example.mzrt.enums.AlertMessage.*;
import static com.example.mzrt.enums.Side.isShort;

public class OrderPriceService {

    /**
     * This method is responsible for determining is the received alert
     * have to be sent
     *
     * @param deal  - The deal within which will search sent orders
     * @param alert - Current entry alert
     * @return True if the received alert is redundant, false if not
     */
    public static boolean isRedundant(Deal deal, Alert alert) {
        if (isEntry(alert.getName())) {
            return currentEntryIsPresent(deal, alert.getName())
                    || higherEntryIsPresent(deal, alert.getName())
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
    private static boolean currentEntryIsPresent(Deal deal, String alert) {
        return switch (AlertMessage.valueByName(alert).getNumber()) {
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
    private static boolean higherEntryIsPresent(Deal deal, String alert) {
        return switch (AlertMessage.valueByName(alert).getNumber()) {
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
        if (deal.getTakePrice4() > 0) {
            return isShort(deal.getSide()) ? STP5 : LTP5;
        } else if (deal.getTakePrice3() > 0) {
            return isShort(deal.getSide()) ? STP4 : LTP4;
        } else if (deal.getTakePrice2() > 0) {
            return isShort(deal.getSide()) ? STP3 : LTP3;
        } else if (deal.getTakePrice1() > 0) {
            return isShort(deal.getSide()) ? STP2 : LTP2;
        } else {
            return isShort(deal.getSide()) ? STP1 : LTP1;
        }
    }

}
