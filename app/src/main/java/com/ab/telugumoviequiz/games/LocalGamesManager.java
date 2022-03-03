package com.ab.telugumoviequiz.games;

import com.ab.telugumoviequiz.common.CallbackResponse;
import com.ab.telugumoviequiz.common.GetTask;
import com.ab.telugumoviequiz.common.Request;
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.main.UserProfile;

import java.util.ArrayList;
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

        mixedEnrolledGameList.start();
        mixedEnrolledGameStatus.start();

        celebrityEnrolledGameList.start();
        celebrityEnrolledGameStatus.start();
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

    public boolean setShowing(int fragmentIndex, boolean showing) {
        List<Object> handlers = getHandlers(fragmentIndex);
        Object obj = handlers.get(0);
        boolean retVal = false;
        if (obj instanceof LocalGameList) {
            retVal = ((LocalGameList) obj).setShowing(showing);
        } else if (obj instanceof LocalLazyGameList) {
            retVal = ((LocalLazyGameList) obj).setShowing(showing);
        }
        LocalGameStatus gameStatus = (LocalGameStatus) handlers.get(1);
        gameStatus.setShowing(showing);
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
}
