package com.ab.telugumoviequiz.games;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalGameList implements CallbackResponse {
    private boolean exceptionThrown;
    private boolean isAPIException;
    private Object helperObject;
    private Object response;
    private final List<GameDetails> cachedGameList = new ArrayList<>();
    private boolean showing;

    private final GetTask<GameDetails[]> getTask;
    private CallbackResponse callbackResponse;

    private ScheduledFuture<?> fetchTask;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int request_status = 0;

    public LocalGameList(GetTask<GameDetails[]> getTask) {
        this.getTask = getTask;
        this.getTask.setCallbackResponse(this);
    }

    public void setCallbackResponse(CallbackResponse callbackResponse) {
        this.callbackResponse = callbackResponse;
    }

    public void start() {
        fetchTask = Scheduler.getInstance().submitRepeatedTask(getTask, 0, 5, TimeUnit.MINUTES);
    }
    public void stop() {
        if (fetchTask != null) {
            fetchTask.cancel(true);
        }
    }
    public boolean setShowing(boolean showing) {
        this.showing = showing;
        lock.readLock().lock();
        if (request_status == 0) {
            lock.readLock().unlock();
            return true;
        } else {
            // Return the cached data here
            sendData();
        }
        lock.readLock().unlock();
        return false;
    }
    public void refreshNow() {
        Scheduler.getInstance().submit(this.getTask);
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown,
                               boolean isAPIException, Object response,
                               Object userObject) {
        request_status = 1;
        this.exceptionThrown = exceptionThrown;
        this.isAPIException = isAPIException;
        this.helperObject = userObject;

        if (exceptionThrown) {
            this.response = response;
            sendData();
            return;
        }

        lock.writeLock().lock();
        cachedGameList.clear();
        List<GameDetails> result = Arrays.asList((GameDetails[]) response);
        cachedGameList.addAll(result);
        lock.writeLock().unlock();
        sendData();
    }

    private void sendData() {
        if (showing) {
            Object callbackResponseObj = response;
            if (!exceptionThrown) {
                callbackResponseObj = cachedGameList.toArray(new GameDetails[0]);
            }
            callbackResponse.handleResponse(getTask.getRequestId(), exceptionThrown,
                    isAPIException, callbackResponseObj, helperObject);
        }
    }
}
