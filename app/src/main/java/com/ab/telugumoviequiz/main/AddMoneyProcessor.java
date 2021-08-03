package com.ab.telugumoviequiz.main;

import android.app.Activity;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.money.TransferRequest;

public class AddMoneyProcessor {
    private static AddMoneyProcessor instance;

    private AddMoneyProcessor() {
    }

    public static AddMoneyProcessor getInstance() {
        if (instance == null) {
            instance = new AddMoneyProcessor();
        }
        return instance;
    }

    public void processAddMoneyRequest(int amount, CallbackResponse callbackResponse, Activity activity) {
        PostTask<TransferRequest, Boolean> addMoneyRequest = Request.getLoadMoneyRequest(amount);
        addMoneyRequest.setCallbackResponse(callbackResponse);
        addMoneyRequest.setPostObject(new TransferRequest());
        addMoneyRequest.setActivity(activity, null);
        Scheduler.getInstance().submit(addMoneyRequest);
    }
}
