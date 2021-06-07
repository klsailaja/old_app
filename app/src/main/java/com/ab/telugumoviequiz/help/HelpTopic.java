package com.ab.telugumoviequiz.help;

import java.util.List;

public class HelpTopic {
    private final String topicHeading;
    private final List<HelpMessage> topicMessages;

    public HelpTopic(String topicHeading, List<HelpMessage> topicMessages) {
        this.topicHeading = topicHeading;
        this.topicMessages = topicMessages;
    }

    public String getTopicHeading() {
        return topicHeading;
    }

    public List<HelpMessage> getTopicMessages() {
        return topicMessages;
    }
}
