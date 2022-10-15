package com.ab.telugumoviequiz.games;

import android.annotation.SuppressLint;

import com.ab.telugumoviequiz.chat.ChatGameDetails;
import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.MessageListener;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.main.UserProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LocalGamesManager {
    private static LocalGamesManager instance;

    private LocalLazyGameList mixedGameList;
    private LocalGameStatus mixedGameStatus;

    private LocalLazyGameList celebrityGameList;
    private LocalGameStatus celebrityGameStatus;

    private LocalGameList mixedEnrolledGameList;
    private LocalGameStatus mixedEnrolledGameStatus;

    private LocalGameList celebrityEnrolledGameList;
    private LocalGameStatus celebrityEnrolledGameStatus;

    private LocalGamesManager() {
    }

    public static LocalGamesManager getInstance() {
        if (instance == null) {
            instance = new LocalGamesManager();
        }
        return instance;
    }

    public void setCallbackResponse(CallbackResponse callbackResponse) {
        mixedGameList.setCallbackResponse(callbackResponse);
        mixedGameStatus.setCallbackResponse(callbackResponse);

        celebrityGameList.setCallbackResponse(callbackResponse);
        celebrityGameStatus.setCallbackResponse(callbackResponse);

        mixedEnrolledGameList.setCallbackResponse(callbackResponse);
        mixedEnrolledGameStatus.setCallbackResponse(callbackResponse);

        celebrityEnrolledGameList.setCallbackResponse(callbackResponse);
        celebrityEnrolledGameStatus.setCallbackResponse(callbackResponse);
    }

    public void initialize() {
        // Mixed Games
        GetTask<GameDetails[]> getMixedTask = Request.getFutureGames();
        mixedGameList = new LocalLazyGameList(getMixedTask, 1);
        GetTask<GameStatusHolder> getMixedGamesStatusTask =
                 Request.getFutureGamesStatusTask(1);
        mixedGameStatus = new LocalGameStatus(getMixedGamesStatusTask);

        // Celebrity Games
        GetTask<GameDetails[]> getCelebrityTask = Request.getFutureGames();
        celebrityGameList = new LocalLazyGameList(getCelebrityTask, 2);
        GetTask<GameStatusHolder> getCelebrityGamesStatusTask =
                Request.getFutureGamesStatusTask(2);
        celebrityGameStatus = new LocalGameStatus(getCelebrityGamesStatusTask);

        // Enrolled Mixed Games
        UserProfile userProfile = UserDetails.getInstance().getUserProfile();
        long userProfileId = -1;
        if (userProfile != null) {
            userProfileId = userProfile.getId();
        }
        GetTask<GameDetails[]> getEnrolledTask = Request.getEnrolledGames(1,userProfileId);
        mixedEnrolledGameList = new LocalGameList(getEnrolledTask);
        GetTask<GameStatusHolder> getEnrolledGamesStatusTask =
                Request.getEnrolledGamesStatus(1, userProfileId);
        mixedEnrolledGameStatus = new LocalGameStatus(getEnrolledGamesStatusTask);

        // Enrolled Celebrity Games
        GetTask<GameDetails[]> getEnrolledCelebrityTask = Request.getEnrolledGames(2,userProfileId);
        celebrityEnrolledGameList = new LocalGameList(getEnrolledCelebrityTask);
        GetTask<GameStatusHolder> getEnrolledCelebrityGamesStatusTask =
                Request.getEnrolledGamesStatus(2, userProfileId);
        celebrityEnrolledGameStatus = new LocalGameStatus(getEnrolledCelebrityGamesStatusTask);
    }

    public List<Object> getHandlers(int fragmentIndex) {
        List<Object> handlers = new ArrayList<>();
        switch (fragmentIndex) {
            case 1: {
                handlers.add(mixedGameList);
                handlers.add(mixedGameStatus);
                break;
            }
            case 2: {
                handlers.add(celebrityGameList);
                handlers.add(celebrityGameStatus);
                break;
            }
            case 3: {
                handlers.add(mixedEnrolledGameList);
                handlers.add(mixedEnrolledGameStatus);
                break;
            }
            case 4: {
                handlers.add(celebrityEnrolledGameList);
                handlers.add(celebrityEnrolledGameStatus);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + fragmentIndex);
        }
        return handlers;
    }

    public void start() {
        mixedGameList.start();
        mixedGameStatus.start();

        celebrityGameList.start();
        celebrityGameStatus.start();

        /*mixedEnrolledGameList.start();
        mixedEnrolledGameStatus.start();

        celebrityEnrolledGameList.start();
        celebrityEnrolledGameStatus.start();*/
    }

    public void stop() {
        mixedGameList.stop();
        mixedGameStatus.stop();

        celebrityGameList.stop();
        celebrityGameStatus.stop();

        mixedEnrolledGameList.stop();
        mixedEnrolledGameStatus.stop();

        celebrityEnrolledGameList.stop();
        celebrityEnrolledGameStatus.stop();
    }

    public void destroy() {
        stop();
        mixedGameList.destroy();
        celebrityGameList.destroy();

        mixedEnrolledGameList.destroy();
        celebrityEnrolledGameList.destroy();
    }

    public boolean setShowing(int fragmentIndex, boolean showing) {
        List<Object> handlers = getHandlers(fragmentIndex);
        Object obj = handlers.get(0);
        boolean retVal = false;
        if (obj instanceof LocalGameList) {
            retVal = ((LocalGameList) obj).setShowing(showing);
            LocalGameStatus gameStatus = (LocalGameStatus) handlers.get(1);
            gameStatus.setShowing(showing);
            if (showing) {
                gameStatus.start();
            } else {
                gameStatus.stop();
            }
        } else if (obj instanceof LocalLazyGameList) {
            retVal = ((LocalLazyGameList) obj).setShowing(showing);
            LocalGameStatus gameStatus = (LocalGameStatus) handlers.get(1);
            gameStatus.setShowing(showing);
        }

        return retVal;
    }

    public void refreshNow(int fragmentIndex) {
        List<Object> handlers = getHandlers(fragmentIndex);
        Object obj = handlers.get(0);
        if (obj instanceof LocalGameList) {
            ((LocalGameList) obj).refreshNow();
        } else if (obj instanceof LocalLazyGameList) {
            ((LocalLazyGameList) obj).refreshNow();
        }
    }

    public List<ChatGameDetails> getChatGameDetails(int gameType) {
        List<GameDetails> cachedGames;
        GameStatusHolder cachedStatus;
        if (gameType == 1) {
            cachedGames = mixedGameList.getCachedGameList();
            cachedStatus = mixedGameStatus.getGamesStatus();
        } else {
            cachedGames = celebrityGameList.getCachedGameList();
            cachedStatus = celebrityGameStatus.getGamesStatus();
        }

        List<ChatGameDetails> chatGameDetails = new ArrayList<>();
        if ((cachedStatus == null) || (cachedGames.size() == 0)) {
            return chatGameDetails;
        }

        HashMap<Long, GameStatus> statusHashMap = cachedStatus.getVal();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        String timePattern = "hh:mm";

        for (GameDetails gameDetails : cachedGames) {
            Long gameId = gameDetails.getGameId();
            GameStatus gameStatus = statusHashMap.get(gameId);
            if (gameStatus != null) {
                gameDetails.setCurrentCount(gameStatus.getCurrentCount());
            }

            ChatGameDetails chatGameDetailsObj = new ChatGameDetails();
            chatGameDetailsObj.setTempGameId(gameDetails.getTempGameId());
            chatGameDetailsObj.setTicketRate(gameDetails.getTicketRate());
            chatGameDetailsObj.setCurrentCount(gameDetails.getCurrentCount());
            chatGameDetailsObj.setGameType(gameType);
            chatGameDetailsObj.setCelebrityName(gameDetails.getCelebrityName());
            chatGameDetailsObj.setGameTimeInMillis(gameDetails.getStartTime());

            simpleDateFormat.applyPattern(timePattern);
            String timeStr = simpleDateFormat.format(new Date(gameDetails.getStartTime()));
            chatGameDetailsObj.setGameTime(timeStr);

            chatGameDetails.add(chatGameDetailsObj);
        }
        return chatGameDetails;
    }

    public void addDataListeners(MessageListener listener) {
        mixedGameList.addDataListeners(listener);
        celebrityGameList.addDataListeners(listener);
    }

    public void removeDataListeners(MessageListener listener) {
        mixedGameList.removeDataListeners(listener);
        celebrityGameList.removeDataListeners(listener);
    }
}
