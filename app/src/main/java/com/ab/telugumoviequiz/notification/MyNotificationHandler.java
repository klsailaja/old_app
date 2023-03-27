package com.ab.telugumoviequiz.notification;

import android.content.Context;

public class MyNotificationHandler {
    private static MyNotificationHandler instance = null;
    private MyAppsNotificationManager myAppsNotificationManager;

    private MyNotificationHandler() {
    }

    public static MyNotificationHandler getInstance(Context context) {
        if (instance == null) {
            instance = new MyNotificationHandler();
            instance.myAppsNotificationManager = new MyAppsNotificationManager(context);
        }
        return instance;
    }

    public void registerNotificationChannelChannel(String channelId, String channelNews,
                                                    String channelDescription) {
        myAppsNotificationManager.registerNotificationChannelChannel(channelId, channelNews,
                channelDescription);
    }

    public void triggerNotification(Class targetNotificationActivity, String channelId, String title, String text, String bigText, int priority, boolean autoCancel, int notificationId, int pendingIntentFlag){
        myAppsNotificationManager.triggerNotification(targetNotificationActivity,channelId,title,text, bigText, priority, autoCancel,notificationId, pendingIntentFlag);
    }

    /*public void triggerNotification(Class targetNotificationActivity, String channelId, String title, String text, String bigText, int priority, boolean autoCancel, int notificationId){
        myAppsNotificationManager.triggerNotification(targetNotificationActivity,channelId,title,text, bigText, priority, autoCancel,notificationId);
    }

    public void updateNotification(Class targetNotificationActivity,String title,String text, String channelId, int notificationId, String bigpictureString, int pendingIntentflag){
        myAppsNotificationManager.updateWithPicture(targetNotificationActivity, title, text, channelId, notificationId, bigpictureString, pendingIntentflag);
    }*/

    public void cancelNotification(int notificationId) {
        myAppsNotificationManager.cancelNotification(notificationId);
    }
}
