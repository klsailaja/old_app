package com.ab.telugumoviequiz.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AlertReceiver extends BroadcastReceiver implements CallbackResponse {
    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        GetTask<String[]> upcomingGamesCelebrityTask = Request.getUpcomingCelebrityNamesTask(hour);
        upcomingGamesCelebrityTask.setCallbackResponse(this);
        upcomingGamesCelebrityTask.setHelperObject(context);
        Scheduler.getInstance().submit(upcomingGamesCelebrityTask);
    }

    public void handleResponse(int reqId, boolean exceptionThrown,
                               boolean isAPIException, Object response, Object userObject) {
        if (exceptionThrown) {
            System.out.println("exceptionThrown");
            return;
        }
        if (isAPIException) {
            System.out.println("isAPIException");
            return;
        }

        if (reqId == Request.UPCOMING_CELEBRITY_NAMES_ID) {
            List<String> results = Arrays.asList((String[]) response);
            String notifySub = results.get(0) + "\n" + results.get(1);

            Context context = (Context) userObject;
            NotificationHelper notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification(notifySub);
            notificationHelper.getManager().notify(1, nb.build());
        }
    }
}
