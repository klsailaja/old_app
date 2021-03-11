package com.ab.telugumoviequiz.common;

import com.ab.telugumoviequiz.main.UserMoney;
import com.ab.telugumoviequiz.main.UserProfile;

public class UserDetails {
    private static UserDetails userDetails = null;
    private UserMoney userMoney = null;

    private UserProfile userProfile;
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

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = null;
        this.userProfile = userProfile;
    }
    public UserProfile getUserProfile() {
        return userProfile;
    }
}
