package com.ab.telugumoviequiz.main;

import android.app.Activity;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.money.LoadMoney;

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

    public void processAddMoneyRequest(LoadMoney loadMoney, CallbackResponse callbackResponse, Activity activity) {
        PostTask<LoadMoney, Boolean> addMoneyRequest = Request.getLoadMoneyRequest();
        addMoneyRequest.setCallbackResponse(callbackResponse);
        addMoneyRequest.setActivity(activity, null);
        addMoneyRequest.setPostObject(loadMoney);
        Scheduler.getInstance().submit(addMoneyRequest);
    }
}
