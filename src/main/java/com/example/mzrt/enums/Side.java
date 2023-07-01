package com.example.mzrt.enums;

import java.util.HashMap;
import java.util.Map;

public enum Side {
    SHORT("Short", "sell"),
    LONG("Long", "buy");

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

    Side(String name, String action) {
        this.name = name;
        this.action = action;
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
     * This method is determines if the current side is a short pos ar a long.
     *
     * @param side - 'sell/buy' for the old deals, 'Short/Long' for the new one
     * @return True if it's SHORT, false if it's LONG
     */
    public static boolean isShort(String side) {
        Side sideByAction = valueByAction(side);
        Side sideByName = valueByName(side);

        return sideByAction == Side.SHORT || sideByName == Side.SHORT;
    }

}
