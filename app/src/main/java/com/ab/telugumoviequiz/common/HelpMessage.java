package com.ab.telugumoviequiz.common;

public class HelpMessage {
    private String message;
    private int msgSeverity;

    public HelpMessage(String message, int msgSeverity) {
        this.message = message;
        this.msgSeverity = msgSeverity;
    }

    public String getMessage() {
        return message;
    }

    public int getMsgSeverity() {
        return msgSeverity;
    }
}
