package com.ab.telugumoviequiz.help;

import java.util.List;

public class HelpMessage {
    private final String message;
    private final int msgSeverity;
    private List<HelpMessage> secondLevelMessages;

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

    public void setSecondLevelMessages(List<HelpMessage> secondLevelMessages) {
        this.secondLevelMessages = secondLevelMessages;
    }

    public List<HelpMessage> getSecondLevelMessages() {
        return secondLevelMessages;
    }
}
