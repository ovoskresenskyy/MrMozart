package com.example.mzrt.enums;

import java.util.HashMap;
import java.util.Map;

public enum AlertMessage {

    STOP_LINE_CHANGE("Stop Line Change"),
    STOP_TREND("Stop Trend");

    private static final Map<String, AlertMessage> BY_NAME = new HashMap<>();

    static {
        for (AlertMessage e : values()) {
            BY_NAME.put(e.name, e);
        }
    }

    public final String name;

    AlertMessage(String name) {
        this.name = name;
    }

    /**
     * This method simply returns the value of enum by the received name
     *
     * @param name - Name of the alert received in the message
     * @return The enum if the matched side
     */
    private static AlertMessage valueByName(String name) {
        return BY_NAME.get(name);
    }

    /**
     * This method determines is the received message told to stop lossing the value
     *
     * @param message - Received message in the alert
     * @return True if it's StopLoss alert, false if not
     */
    public static boolean isStopLoss(String message) {
        return valueByName(message) == STOP_LINE_CHANGE;
    }

    /**
     * This method determines is the received message told to stop the trend
     *
     * @param message - Received message in the alert
     * @return True if it's StopTrend alert, false if not
     */
    public static boolean isStopTrend(String message) {
        return valueByName(message) == STOP_TREND;
    }

}
