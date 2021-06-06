package com.ab.telugumoviequiz.help;

public class HelpMessage {
    private final String message;
    private final int msgSeverity;

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
