package com.ab.telugumoviequiz.common;

import com.ab.telugumoviequiz.main.UserProfile;

public class UserDetails {
    private static UserDetails userDetails = null;

    private UserProfile userProfile;
    private UserDetails() {
    }

    public static UserDetails getInstance() {
        if (userDetails == null) {
            userDetails = new UserDetails();
        }
        return userDetails;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = null;
        this.userProfile = userProfile;
    }
    public UserProfile getUserProfile() {
        return userProfile;
    }
}
