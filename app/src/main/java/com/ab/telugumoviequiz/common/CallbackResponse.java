package com.ab.telugumoviequiz.common;

public interface CallbackResponse {
    void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, Object response, Object userObject);
}
