package com.ab.telugumoviequiz.common;

import android.app.Activity;
import android.content.Intent;

import com.ab.telugumoviequiz.main.LoginActivity;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;

public class SwitchScreen implements DialogAction {

    public static final int LOGIN_PAGE = 1;
    public static final int GAMES_PAGE = 2;
    private final Activity mainActivity;

    public SwitchScreen(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void doAction(int id, Object userObj) {
        if (id == LOGIN_PAGE) {
            Intent intent = new Intent(mainActivity, LoginActivity.class);
            mainActivity.startActivity(intent);
            mainActivity.finish();
            return;
        }
        if (mainActivity instanceof MainActivity) {
            ((MainActivity) mainActivity).launchView(Navigator.CURRENT_GAMES, null, false);
        }
    }

}
