package com.ab.telugumoviequiz.common;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.main.LoginActivity;

public class SwitchScreen implements DialogAction {
    private final Activity mainActivity;

    public SwitchScreen(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void doAction(int id, Object userObj) {
        AlertDialog alertDialog = Utils.getProgressDialog(mainActivity, Utils.WAIT_MESSAGE);
        alertDialog.show();
        Intent intent = new Intent(mainActivity, LoginActivity.class);
        mainActivity.startActivity(intent);
        mainActivity.finish();
        alertDialog.dismiss();
        Utils.shutdown(mainActivity.getResources().getString(R.string.base_url));
    }
}
