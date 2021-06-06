package com.ab.telugumoviequiz.help;

import java.util.List;

public class HelpTopic {
    private String topicHeading;
    private List<HelpMessage> topicMessages;

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
