package com.ab.telugumoviequiz.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WinMsgHandler implements CallbackResponse {

    private static WinMsgHandler instance;
    private final List<String> winWdMessages = new ArrayList<>();
    private long userProfileId = -1;
    private MessageListener listener;
    private boolean isStopped = false;
    private int count;

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
        fetchData();
    }

    public void stop() {
        isStopped = true;
    }

    public void setUserProfileId(long userProfileId1) {
        System.out.println("In setUserProfileId" + userProfileId1);
        this.userProfileId = userProfileId1;
        fetchData();
    }

    private void fetchData() {
        GetTask<String[]> getMsgTask = Request.getWinWdMessages(userProfileId);
        getMsgTask.setCallbackResponse(this);
        Scheduler.getInstance().submit(getMsgTask);
    }

    public void setListener(MessageListener messageListener) {
        this.listener = messageListener;
    }
    private void notifyMessage(String msg) {
        if (isStopped) {
            return;
        }
        if (listener != null) {
            List<String> msgList = new ArrayList<>();
            msgList.add(msg);
            listener.passData(999, msgList);
        }
    }

    public void handleResponse(int reqId, boolean exceptionThrown, boolean isAPIException, final Object response, Object helperObject) {
        if((exceptionThrown) && (!isAPIException)) {
            System.out.println("Exception thrown here " + response);
            return;
        }
        if (isAPIException) {
            System.out.println("isAPIException thrown here " + response);
            return;
        }
        if (reqId == Request.WIN_WD_MSGS) {
            List<String> result = Arrays.asList((String[]) response);
            winWdMessages.clear();
            winWdMessages.addAll(result);
            int size = winWdMessages.size();
            System.out.println("size is " + size);
            String errMessage;
            if (size == 0) {
                errMessage = "No Data";
            } else {
                errMessage = winWdMessages.get(0);
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
                return;
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
