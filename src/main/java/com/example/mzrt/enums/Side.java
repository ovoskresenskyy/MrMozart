package com.example.mzrt.enums;

import java.util.HashMap;
import java.util.Map;

public enum Side {
    SHORT("Short", "sell"),
    LONG("Long", "buy");

    private static final Map<String, Side> BY_ACTION = new HashMap<>();

    static {
        for (Side e : values()) {
            BY_ACTION.put(e.action, e);
        }
    }

    public final String name;
    public final String action;

    Side(String name, String action) {
        this.name = name;
        this.action = action;
    }

    public static Side valueByAction(String action) {
        return BY_ACTION.get(action);
    }
}
