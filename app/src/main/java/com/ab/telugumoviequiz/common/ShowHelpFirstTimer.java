package com.ab.telugumoviequiz.common;

import java.util.HashMap;
import java.util.Map;

public class ShowHelpFirstTimer {
    private static ShowHelpFirstTimer instance;
    private Map<String, Integer> firstTimeStatus = new HashMap<>();

    private ShowHelpFirstTimer() {
    }

    public static ShowHelpFirstTimer getInstance() {
        if (instance == null) {
            instance = new ShowHelpFirstTimer();
        }
        return instance;
    }

    public boolean isFirstTime(String helpViewKey) {
        Integer state = firstTimeStatus.get(helpViewKey);
        if (state == null) {
            firstTimeStatus.put(helpViewKey, 1);
            return true;
        }
        return false;
    }
    public void destroy() {
        firstTimeStatus.clear();
    }
}
