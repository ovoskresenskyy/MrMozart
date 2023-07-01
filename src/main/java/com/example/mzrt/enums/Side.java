package com.example.mzrt.enums;

import java.util.HashMap;
import java.util.Map;

public enum Side {
    SHORT("Short", "sell", "SSL"),
    LONG("Long", "buy", "LSL");

    private static final Map<String, Side> BY_ACTION = new HashMap<>();
    private static final Map<String, Side> BY_NAME = new HashMap<>();

    static {
        for (Side e : values()) {
            BY_ACTION.put(e.action, e);
            BY_NAME.put(e.name, e);
        }
    }

    public final String name;
    public final String action;
    public final String closingAlert;

    Side(String name, String action, String closingAlert) {
        this.name = name;
        this.action = action;
        this.closingAlert = closingAlert;
    }

    /**
     * This method simply returns the value of enum by the received action (side)
     *
     * @param action - 'sell/buy'
     * @return The enum if the matched side
     */
    public static Side valueByAction(String action) {
        return BY_ACTION.get(action);
    }

    /**
     * This method simply returns the value of enum by the received name (side)
     *
     * @param name - 'Short/Long'
     * @return The enum if the matched side
     */
    public static Side valueByName(String name) {
        return BY_NAME.get(name);
    }

    /**
     * This method simply returns the value of enum by the received side
     * No matter is it new or the old one
     *
     * @param side - 'Short/Long' or 'sell/buy'
     * @return The enum if the matched side
     */
    public static Side valueBySide(String side) {
        Side sideByAction = valueByAction(side);
        if (sideByAction == null) {
            return valueByName(side);
        }
        return sideByAction;
    }

    /**
     * This method is determines if the current side is a short pos ar a long.
     *
     * @param side - 'sell/buy' for the old deals, 'Short/Long' for the new one
     * @return True if it's SHORT, false if it's LONG
     */
    public static boolean isShort(String side) {
        return valueBySide(side) == Side.SHORT;
    }

    /**
     * This method returns the closing aleert according to the received side
     *
     * @param side - 'sell/buy' for the old deals, 'Short/Long' for the new one
     * @return SSL if it's Short/sell, LSL if it's Long/buy
     */
    public static String getClosingAlert(String side) {
        return valueBySide(side).closingAlert;
    }
}
