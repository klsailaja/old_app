package com.ab.telugumoviequiz.common;

import java.util.List;

public interface MessageListener {
    void passData(int reqId, List<String> data);
}
