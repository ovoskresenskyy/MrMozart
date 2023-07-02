package com.example.mzrt.enums;

import java.util.HashMap;
import java.util.Map;

public enum Side {
    SHORT("Short", "S", "sell", "SSL"),
    LONG("Long", "L", "buy", "LSL"),
    EMPTY("", "", "", "");

    private static final Map<String, Side> BY_ACTION = new HashMap<>();
    private static final Map<String, Side> BY_NAME = new HashMap<>();

    static {
        for (Side e : values()) {
            BY_ACTION.put(e.action, e);
            BY_NAME.put(e.name, e);
        }
    }

    public final String name;
    public final String shortName;
    public final String action;
    public final String closingAlert;

    Side(String name, String shortName, String action, String closingAlert) {
        this.name = name;
        this.shortName = shortName;
        this.action = action;
        this.closingAlert = closingAlert;
    }

    /**
     * This method simply returns the value of enum by the received action (side)
     *
     * @param action - 'sell/buy'
     * @return The enum if the matched side
     */
    private static Side valueByAction(String action) {
        return BY_ACTION.get(action);
    }

    /**
     * This method simply returns the value of enum by the received name (side)
     *
     * @param name - 'Short/Long'
     * @return The enum if the matched side
     */
    private static Side valueByName(String name) {
        return BY_NAME.get(name);
    }

    /**
     * This method simply returns the value of enum by the received name
     * No matter is it new or the old one
     *
     * @param name - 'Short/Long' or 'sell/buy'
     * @return The enum if the matched name
     */
    public static Side sideByName(String name) {
        Side sideByAction = valueByAction(name);
        if (sideByAction == null) {
            return valueByName(name);
        }
        return sideByAction;
    }

    /**
     * This method determines value by the part of the received message
     *
     * @param message - Received text in the alert message
     * @return Matched side
     */
    public static Side getSideByMessage(String message) {
        if (message.indexOf("long trend") > 0) {
            return LONG;
        } else if (message.indexOf("short trend") > 0) {
            return SHORT;
        }
        return EMPTY;
    }

    /**
     * This method is determines if the current side is a short pos ar a long.
     *
     * @param side - 'sell/buy' for the old deals, 'Short/Long' for the new one
     * @return True if it's SHORT, false if it's LONG
     */
    public static boolean isShort(String side) {
        return sideByName(side) == Side.SHORT;
    }

    /**
     * This method returns the closing aleert according to the received side
     *
     * @param side - 'sell/buy' for the old deals, 'Short/Long' for the new one
     * @return SSL if it's Short/sell, LSL if it's Long/buy
     */
    public static String getClosingAlert(String side) {
        return sideByName(side).closingAlert;
    }
}
