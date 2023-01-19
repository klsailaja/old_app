package com.ab.telugumoviequiz.games;

import android.util.Log;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.PostTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.money.MoneyStatusInput;
import com.ab.telugumoviequiz.money.MoneyStatusOutput;

import java.util.Date;

public class CancelGameStatusTask implements Runnable {
    private long gameSlotTime;
    private CallbackResponse handler;
    private static CancelGameStatusTask instance = null;

    private CancelGameStatusTask() {
    }

    public static CancelGameStatusTask getInstance(long initialGameSlotTime,
                                                   CallbackResponse handler) {
        if (instance == null) {
            instance = new CancelGameStatusTask();
            instance.gameSlotTime = initialGameSlotTime;
            instance.handler = handler;
        }
        return instance;
    }

    public void run() {
        PostTask<MoneyStatusInput, MoneyStatusOutput> cancelGamesRefundMoneyTask =
                Request.getCancelGamesRefundMoneyStatusTask();
        cancelGamesRefundMoneyTask.setCallbackResponse(handler);
        String details = gameSlotTime + ":" + 1;
        cancelGamesRefundMoneyTask.setHelperObject(details);

        MoneyStatusInput input = new MoneyStatusInput();
        input.setGameSlotTime(gameSlotTime);
        input.setOperType(2);
        input.setUid(UserDetails.getInstance().getUserProfile().getId());
        cancelGamesRefundMoneyTask.setPostObject(input);

        Log.d("Cancel Task", gameSlotTime + " : " + new Date(gameSlotTime));
        Scheduler.getInstance().submit(cancelGamesRefundMoneyTask);
        gameSlotTime = gameSlotTime + (5 * 60 * 1000);
    }
}
