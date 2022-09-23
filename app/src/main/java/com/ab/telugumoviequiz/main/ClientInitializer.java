package com.ab.telugumoviequiz.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.MessageListener;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.games.LocalGamesManager;

public class ClientInitializer implements CallbackResponse {
    private final String TAG = "ClientInitializer:";
    @SuppressLint("StaticFieldLeak")
    private static ClientInitializer instance;
    private final Activity activity;

    private long currentlyLoggedInUserCount;
    private final MessageListener messageListener;
    private AlertDialog alertDialog;



    private ClientInitializer(Activity activity, MessageListener messageListener) {
        this.activity = activity;
        this.messageListener = messageListener;
    }

    private void initialize() {
        Log.d(TAG, "In initialize");
        Runnable run = () -> {
            alertDialog = Utils.getProgressDialog(activity, Utils.WAIT_MESSAGE);
            alertDialog.show();
        };
        activity.runOnUiThread(run);
        GetTask<String> timeCheckTask = Request.getTimeCheckTask();
        timeCheckTask.setCallbackResponse(this);
        Scheduler.getInstance().submit(timeCheckTask);
    }

    public static ClientInitializer getInstance(Activity activity,
                                                MessageListener messageListener) {
        if (instance == null) {
            instance = new ClientInitializer(activity, messageListener);
            instance.initialize();
        }
        return instance;
    }

    public long getLoggedInUserCount() {
        return currentlyLoggedInUserCount;
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException,
                               Object response, Object userObject) {
        if ((exceptionThrown) && (!isAPIException)) {
            alertDialog.dismiss();
            Runnable run = () -> Utils.showMessage("Error", (String)response, activity, null);
            activity.runOnUiThread(run);
            return;
        }
        if (isAPIException) {
            alertDialog.dismiss();
            Runnable run = () -> Utils.showMessage("Error", (String)response, activity, null);
            activity.runOnUiThread(run);
            return;
        }
        if (Request.TIME_CHECK_ID == reqId) {
            String result = (String) response;
            Log.d(TAG, "Time Check Req response:" + result);
            Resources resources = activity.getResources();
            String errorMsg = resources.getString(R.string.time_sync_error);
            if (result.equalsIgnoreCase("false")) {
                Runnable run = () -> Utils.showMessage("Error", errorMsg, activity, null);
                activity.runOnUiThread(run);
            } else {
                LocalGamesManager.getInstance().initialize();
                LocalGamesManager.getInstance().start();

                GetTask<Long> loggedInUserCtTask = Request.getLoggedInUserCount();
                loggedInUserCtTask.setCallbackResponse(this);
                Scheduler.getInstance().submit(loggedInUserCtTask);
            }
        } else if (Request.GET_LOGGEG_IN_USER_COUNT == reqId) {
            currentlyLoggedInUserCount = (Long) response;
            Log.d(TAG, "User Count response:" + currentlyLoggedInUserCount);

            UserProfile userProfile = UserDetails.getInstance().getUserProfile();
            GetTask<UserMoney> fetchMoney = Request.getMoneyTask(userProfile.getId());
            fetchMoney.setCallbackResponse(this);
            Scheduler.getInstance().submit(fetchMoney);
        } else if (Request.GET_USER_MONEY == reqId) {
            UserMoney userMoney = (UserMoney) response;
            Log.d(TAG, "User Money Request response:" + userMoney);
            UserDetails.getInstance().setUserMoney(userMoney);
            Runnable run = () -> alertDialog.dismiss();
            activity.runOnUiThread(run);
            messageListener.passData(MessageListener.QUIZ_SEVER_VERIFIED, null);
        }

    }
}
