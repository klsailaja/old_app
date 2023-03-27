package com.ab.telugumoviequiz.main;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;
import com.ab.telugumoviequiz.notification.MyNotificationHandler;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AlertReceiver extends BroadcastReceiver implements CallbackResponse {
    @Override
    public void onReceive(Context context, Intent intent) {

        String coreserverIp = context.getResources().getString(R.string.base_url);
        GetTask<String[]> upcomingGamesCelebrityTask =
                Request.getUpcomingCelebrityNamesTask(coreserverIp);
        upcomingGamesCelebrityTask.setCallbackResponse(this);
        upcomingGamesCelebrityTask.setHelperObject(context);
        Scheduler.getInstance().submit(upcomingGamesCelebrityTask);
    }

    public void handleResponse(int reqId, boolean exceptionThrown,
                               boolean isAPIException, Object response, Object userObject) {
        if (exceptionThrown) {
            return;
        }
        if (isAPIException) {
            return;
        }

        if (reqId == Request.UPCOMING_CELEBRITY_NAMES_ID) {
            List<String> results = Arrays.asList((String[]) response);
            String notifySub = results.get(0) + "\n" + results.get(1);

            Context context = (Context) userObject;

            MyNotificationHandler.getInstance(context).cancelNotification(1);
            MyNotificationHandler.getInstance(context).triggerNotification(LoginActivity.class,
                    context.getString(R.string.NEWS_CHANNEL_ID),
                    context.getString(R.string.NOTIFICATION_TITLE),
                    context.getString(R.string.NOTIFICATION_DESC),
                    notifySub,
                    NotificationCompat.PRIORITY_HIGH,
                    true,
                    1,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
}
