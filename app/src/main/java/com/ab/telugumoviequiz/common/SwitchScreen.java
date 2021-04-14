package com.ab.telugumoviequiz.common;

import android.app.Activity;
import android.content.Intent;

import com.ab.telugumoviequiz.main.LoginActivity;
import com.ab.telugumoviequiz.main.MainActivity;
import com.ab.telugumoviequiz.main.Navigator;

public class SwitchScreen implements DialogAction {
    private final Activity mainActivity;

    public SwitchScreen(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void doAction(int id, Object userObj) {
        Intent intent = new Intent(mainActivity, LoginActivity.class);
        mainActivity.startActivity(intent);
        mainActivity.finish();
    }
}
