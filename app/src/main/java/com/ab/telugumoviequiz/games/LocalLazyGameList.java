package com.ab.telugumoviequiz.games;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.Constants;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.Scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final AtomicInteger request_status = new AtomicInteger(0);
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
        request_status.set(0);
        exceptionThrown = false;
        isAPIException = false;
        helperObject = null;
        response = null;
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
        if (request_status.get() == 0) {
            lock.readLock().unlock();
            return true;
        } else sendData();
        lock.readLock().unlock();
        return false;
    }
    public void refreshNow() {
        long currentTime = System.currentTimeMillis();
        boolean shouldSendReq = false;
        lock.writeLock().lock();
        Iterator<GameDetails> gameDetailsIterator = cachedGameList.iterator();
        while (gameDetailsIterator.hasNext()) {
            GameDetails oldGB = gameDetailsIterator.next();
            if (oldGB.getStartTime() < currentTime) {
                gameDetailsIterator.remove();
            }
        }
        if (cachedGameList.size() > 0) {
            sendData();
            shouldSendReq = true;
        }
        lock.writeLock().unlock();
        if (shouldSendReq) {
            maxGameId = -1;
            makeRequestReady();
            Scheduler.getInstance().submit(this.getTask);
        }
    }

    @Override
    public void handleResponse(int reqId, boolean exceptionThrown,
                               boolean isAPIException, Object response,
                               Object userObject) {
        request_status.set(1);
        this.exceptionThrown = exceptionThrown;
        this.isAPIException = isAPIException;
        this.helperObject = userObject;

        if (exceptionThrown) {
            this.response = response;
            sendData();
            return;
        }
        List<GameDetails> result = Arrays.asList((GameDetails[]) response);
        boolean callSendData = (result.size() == 0);
        removeOldEntries(callSendData);
        if (result.size() > 0) {
            lock.writeLock().lock();
            TreeMap<Long, Integer> gameIdToListPos = new TreeMap<>();
            for (int dataIndex = 0; dataIndex < cachedGameList.size(); dataIndex ++) {
                GameDetails gd = cachedGameList.get(dataIndex);
                gameIdToListPos.put(gd.getGameId(), dataIndex);
                if (maxGameId < gd.getGameId()) {
                    maxGameId = gd.getGameId();
                }
            }
            for (GameDetails newGD : result) {
                if (maxGameId < newGD.getGameId()) {
                    maxGameId = newGD.getGameId();
                }
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
            Scheduler.getInstance().submit(getTask, Constants.GAMES_POLLER_TIME_IN_SECS, TimeUnit.SECONDS);
        }
    }

    public List<GameDetails> getCachedGameList() {
        return cachedGameList;
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

    private void removeOldEntries(boolean callSendData) {
        lock.writeLock().lock();
        long currentTime = System.currentTimeMillis();
        Iterator<GameDetails> gameDetailsIterator = cachedGameList.iterator();
        while (gameDetailsIterator.hasNext()) {
            GameDetails oldGB = gameDetailsIterator.next();
            if (oldGB.getStartTime() < currentTime) {
                gameDetailsIterator.remove();
            }
        }
        lock.writeLock().unlock();
        if (!callSendData) {
            sendData();
        }
    }
}
