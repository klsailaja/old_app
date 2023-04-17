package com.ab.telugumoviequiz.common;

import com.ab.telugumoviequiz.main.UserMoney;
import com.ab.telugumoviequiz.main.UserProfile;

public class UserDetails {
    private static UserDetails userDetails = null;
    private UserMoney userMoney = null;
    private UserProfile userProfile;

    // These 2 are set by QuestionFragment
    private int lastPlayedGameId = -1;
    private long lastPlayedGameTime = -1;

    // These 2 are set by MainActivity
    private int lastPlayedGameWinMoneyCreditStatus = -1; // 0 means in-progress, 1-complete, 2-error
    private long lastPolledSlotGameTime = -1;
    private String lastPlayedGameWinMoneyCreditMsg;

    private boolean isGameSoundOn = true;
    private boolean notificationValue = true;

    private UserDetails() {
    }

    public static UserDetails getInstance() {
        if (userDetails == null) {
            userDetails = new UserDetails();
        }
        return userDetails;
    }

    public void setUserMoney(UserMoney userMoney) {
        this.userMoney = null;
        this.userMoney = userMoney;
    }
    public UserMoney getUserMoney() {
        return userMoney;
    }

    public static UserDetails getUserDetails() {
        return userDetails;
    }

    public static void setUserDetails(UserDetails userDetails) {
        UserDetails.userDetails = userDetails;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = null;
        this.userProfile = userProfile;
    }
    public UserProfile getUserProfile() {
        return userProfile;
    }

    public int getLastPlayedGameId() {
        return lastPlayedGameId;
    }

    public void setLastPlayedGameId(int lastPlayedGameId) {
        this.lastPlayedGameId = lastPlayedGameId;
    }

    public long getLastPlayedGameTime() {
        return lastPlayedGameTime;
    }

    public void setLastPlayedGameTime(long lastPlayedGameTime) {
        this.lastPlayedGameTime = lastPlayedGameTime;
    }

    public int getLastPlayedGameWinMoneyCreditStatus() {
        return lastPlayedGameWinMoneyCreditStatus;
    }

    public void setLastPlayedGameWinMoneyCreditStatus(int lastPlayedGameWinMoneyCreditStatus) {
        this.lastPlayedGameWinMoneyCreditStatus = lastPlayedGameWinMoneyCreditStatus;
    }

    public void setLastPolledSlotGameTime(long lastPolledSlotGameTime) {
        this.lastPolledSlotGameTime = lastPolledSlotGameTime;
    }
    public long getLastPolledSlotGameTime() {
        return this.lastPolledSlotGameTime;
    }

    public void setLastPlayedGameWinMoneyCreditMsg(String msg) {
        lastPlayedGameWinMoneyCreditMsg = msg;
    }
    public String getLastPlayedGameWinMoneyCreditMsg() {
        return lastPlayedGameWinMoneyCreditMsg;
    }

    public void setIsGameSoundOn(boolean value) {
        isGameSoundOn = value;
    }
    public boolean getIsGameSoundOn() {
        return isGameSoundOn;
    }

    public void setNotificationValue(boolean value) {
        notificationValue = value;
    }
    public boolean getNotificationValue() {
        return notificationValue;
    }

    public boolean isMoneyMode() {
        return userProfile.isMoneyMode();
    }
}
