package com.ab.telugumoviequiz.common;

public class UITask implements Runnable {
    private int requestId;
    private CallbackResponse callbackResponse;
    private Object helperObject;

    public UITask(int requestId, CallbackResponse callbackResponse, Object helperObject) {
        this.requestId = requestId;
        this.callbackResponse = callbackResponse;
        this.helperObject = helperObject;
    }

    @Override
    public void run() {
        this.callbackResponse.handleResponse(requestId, false, false, null, helperObject);
    }
}
