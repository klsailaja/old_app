package com.ab.telugumoviequiz.common;

import android.app.Activity;

import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;

public class ShowHomeScreen implements DialogAction {
    private final Activity mainActivity;

    public ShowHomeScreen(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void doAction(int id, Object userObj) {
        if (mainActivity instanceof MainActivity) {
            ((MainActivity) mainActivity).launchView(Navigator.CURRENT_GAMES, null, false);
        }
    }
}


