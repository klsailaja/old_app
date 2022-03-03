package com.ab.telugumoviequiz.games;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.Constants;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalLazyGameList implements CallbackResponse {
    private boolean exceptionThrown;
    private boolean isAPIException;
    private Object helperObject;
    private Object response;
    private final List<GameDetails> cachedGameList = new ArrayList<>();
    private boolean showing;

    private final GetTask<GameDetails[]> getTask;
    private CallbackResponse callbackResponse;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int request_status = 0;
    private boolean start;
    private long maxGameId = -1;
    private final int gameType;

    // boolean oneShot -> true, polling period -> false, polling period, last game id, new set

    public LocalLazyGameList(GetTask<GameDetails[]> getTask, int gameType) {
        this.gameType = gameType;
        this.getTask = getTask;
        this.getTask.setCallbackResponse(this);
    }

    public void setCallbackResponse(CallbackResponse callbackResponse) {
        this.callbackResponse = callbackResponse;
    }

    public void start() {
        start = true;
        maxGameId = -1;
        makeRequestReady();
        Scheduler.getInstance().submit(getTask);
    }

    public void stop() {
        start = false;
    }

    public boolean setShowing(boolean showing) {
        this.showing = showing;
        lock.readLock().lock();
        // Return the cached data here
        if (request_status == 0) {
            lock.readLock().unlock();
            return true;
        } else sendData();
        lock.readLock().unlock();
        return false;
    }
    public void refreshNow() {
        maxGameId = -1;
        makeRequestReady();
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
        List<GameDetails> result = Arrays.asList((GameDetails[]) response);
        if (result.size() > 0) {
            TreeMap<Long, Integer> gameIdToListPos = new TreeMap<>();
            for (int dataIndex = 0; dataIndex < cachedGameList.size(); dataIndex ++) {
                GameDetails gd = cachedGameList.get(dataIndex);
                gameIdToListPos.put(gd.getGameId(), dataIndex);
                if (maxGameId < gd.getGameId()) {
                    maxGameId = gd.getGameId();
                }
            }
            for (GameDetails newGD : result) {
                Integer listPos = gameIdToListPos.get(newGD.getGameId());
                if (listPos != null) {
                    cachedGameList.set(listPos, newGD);
                } else {
                    cachedGameList.add(newGD);
                }
            }
        }
        lock.writeLock().unlock();
        sendData();
        if (start) {
            makeRequestReady();
            Scheduler.getInstance().submit(getTask, 30, TimeUnit.SECONDS);
        }
    }

    private void sendData() {
        Object callbackResponseObj = response;
        if (!exceptionThrown) {
            callbackResponseObj = cachedGameList.toArray(new GameDetails[0]);
        }
        callbackResponse.handleResponse(getTask.getRequestId(), exceptionThrown,
                isAPIException, callbackResponseObj, helperObject);
    }

    private void makeRequestReady() {
        int fetchSlotSize = Constants.MIXED_INITIAL_SLOT_NUMBERS;
        if (maxGameId == -1) {
            if (gameType == 2) {
                fetchSlotSize = Constants.CELEBRITY_INITIAL_SLOT_NUMBERS;
            }
        } else {
            fetchSlotSize = Constants.MIXED_FETCH_NEXT_SLOT_NUMBERS;
            if (gameType == 2) {
                fetchSlotSize = Constants.CELEBRITY_FETCH_NEXT_SLOT_NUMBERS;
            }
        }
        this.getTask.setReqUri(Request.getFutureGamesURI(gameType, maxGameId, fetchSlotSize));
    }
}
