package com.ab.telugumoviequiz.common;

import java.util.List;

public interface MessageListener {
    int QUIZ_SEVER_VERIFIED = 10000;
    int GAMES_DATA_UPDATED = 10001;

    void passData(int reqId, List<String> data);
}
