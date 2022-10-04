package com.ab.telugumoviequiz.common;

import java.util.List;

public interface MessageListener {
    int QUIZ_SEVER_VERIFIED = 10000;

    void passData(int reqId, List<String> data);
}
