package com.ab.telugumoviequiz.games;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Scheduler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalGameStatus implements CallbackResponse {
    private boolean exceptionThrown;
    private boolean isAPIException;
    private Object helperObject;
    private Object response;
    private GameStatusHolder gameStatusHolder;
    private boolean showing;

    private final GetTask<GameStatusHolder> getTask;
    private CallbackResponse callbackResponse;

    private ScheduledFuture<?> fetchTask;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public LocalGameStatus(GetTask<GameStatusHolder> getTask) {
        this.getTask = getTask;
        this.getTask.setCallbackResponse(this);
    }

    public void setCallbackResponse(CallbackResponse callbackResponse) {
        this.callbackResponse = callbackResponse;
    }

    public void start() {
        fetchTask = Scheduler.getInstance().submitRepeatedTask(getTask, 30, 30, TimeUnit.SECONDS);
    }
    public void stop() {
        if (fetchTask != null) {
            fetchTask.cancel(true);
        }
    }
    public boolean setShowing(boolean showing) {
        this.showing = showing;
        lock.readLock().lock();
        if (gameStatusHolder == null) {
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
        this.exceptionThrown = exceptionThrown;
        this.isAPIException = isAPIException;
        this.helperObject = userObject;

        if (exceptionThrown) {
            this.response = response;
            sendData();
            return;
        }

        lock.writeLock().lock();
        gameStatusHolder = (GameStatusHolder) response;
        lock.writeLock().unlock();
        sendData();
    }
    private void sendData() {
        if (showing) {
            Object callbackResponseObj = gameStatusHolder;
            if (exceptionThrown) {
                callbackResponseObj = response;
            }
            System.out.println(callbackResponseObj);
            callbackResponse.handleResponse(getTask.getRequestId(), exceptionThrown,
                    isAPIException, callbackResponseObj, helperObject);
        }
    }
}
