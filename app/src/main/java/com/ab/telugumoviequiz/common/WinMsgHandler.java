package com.ab.telugumoviequiz.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WinMsgHandler implements CallbackResponse {

    private static WinMsgHandler instance;
    private final List<String> winWdMessages = new ArrayList<>();
    private long userProfileId = -1;
    private MessageListener listener;
    private boolean isStopped = false;
    private int count;
    private long lastFetchTime;
    public static int WIN_MSG_ID = 999;

    private WinMsgHandler() {
    }

    public static WinMsgHandler getInstance() {
        if (instance == null) {
            instance = new WinMsgHandler();
        }
        return instance;
    }

    public void start() {
        isStopped = false;
        count = 0;
        fetchData(20);
    }

    public void setUserProfileId(long userProfileId1) {
        this.userProfileId = userProfileId1;
        fetchData(10);
    }

    private void fetchData(int maxClosedGroupUserCount) {
        GetTask<String[]> getMsgTask = Request.getWinWdMessages(userProfileId, maxClosedGroupUserCount);
        getMsgTask.setCallbackResponse(this);
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int offset = 0;
        if ((minutes % 5) == 0) {
            offset = 2;
        }
        Scheduler.getInstance().submit(getMsgTask, offset, TimeUnit.MINUTES);
    }

    public void setListener(MessageListener messageListener) {
        this.listener = messageListener;
    }
    private void notifyMessage(String msg) {
        if (isStopped) {
            return;
        }
        if (listener != null) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastFetchTime) < 15 * 1000) {
                return;
            }
            List<String> msgList = new ArrayList<>();
            msgList.add(msg);
            listener.passData(WIN_MSG_ID, msgList);
        }
    }

    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        if((exceptionThrown) && (!isAPIException)) {
            return;
        }
        if (isAPIException) {
            return;
        }
        if (reqId == Request.WIN_WD_MSGS) {
            List<String> result = Arrays.asList((String[]) response);
            winWdMessages.clear();
            winWdMessages.addAll(result);
            int size = winWdMessages.size();
            String errMessage;
            if (size == 0) {
                errMessage = "Recent Win messages shown here";
            } else {
                errMessage = winWdMessages.get(0);
                lastFetchTime = System.currentTimeMillis();
            }
            notifyMessage(errMessage);
            UITask task = new UITask(Request.WIN_WD_SHOW_MSG, this, 1);
            Scheduler.getInstance().submit(task, 15 * 1000, TimeUnit.MILLISECONDS);
        } else if (Request.WIN_WD_SHOW_MSG == reqId) {
            if (isStopped) {
                return;
            }
            count++;
            if (count >= 120) {
                start();
            }
            Integer currentIndex = (Integer) helperObject;
            if (currentIndex >= winWdMessages.size()) {
                currentIndex = 0;
            }
            notifyMessage(winWdMessages.get(currentIndex));
            UITask task = new UITask(Request.WIN_WD_SHOW_MSG, this, currentIndex + 1);
            Scheduler.getInstance().submit(task, 15 * 1000, TimeUnit.MILLISECONDS);
        }
    }
}
