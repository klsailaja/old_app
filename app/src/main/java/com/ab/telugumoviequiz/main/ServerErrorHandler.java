package com.ab.telugumoviequiz.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.Keys;
import com.ab.telugumoviequiz.common.Utils;

import java.util.ArrayList;
import java.util.List;

public class ServerErrorHandler implements DialogAction {
    public static int APP_SHUTDOWN = -90;
    private final List<DialogAction> shutDownListeners = new ArrayList<>();
    private Activity mainActivity;
    @SuppressLint("StaticFieldLeak")
    private static ServerErrorHandler instance;
    private boolean showing = false;
    private final String TAG = "ServerErrorHandler";

    private ServerErrorHandler() {
    }

    public static ServerErrorHandler getInstance() {
        if (instance == null) {
            instance = new ServerErrorHandler();
        }
        return instance;
    }
    public boolean handleServerError(String title, String msg, Activity activity) {
        if (showing) {
            return true;
        }
        showing = true;
        Runnable run = () -> Utils.showMessage(title, msg, activity, this);
        activity.runOnUiThread(run);
        mainActivity = activity;
        return true;
    }
    public void destroy() {
        instance = null;
    }
    public void addShutdownListener(DialogAction listener) {
    }
    public void removeShutdownListener(DialogAction listener) {
    }
    private void notifyListeners() {
        for (DialogAction listener : shutDownListeners) {
            Log.d(TAG, "Notify : " + listener.getClass().getName());
            try {
                listener.doAction(APP_SHUTDOWN, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void doAction(int calledId, Object userObject) {
        shutDownApp(mainActivity);
    }

    public void shutDownApp(Activity mainActivity) {
        AlertDialog alertDialog = Utils.getProgressDialog(mainActivity, Utils.WAIT_MESSAGE);
        Log.d(TAG, "This is after dialog show");
        alertDialog.show();
        notifyListeners();
        String coreServerUrl = mainActivity.getResources().getString(R.string.base_url);
        Utils.shutdown(coreServerUrl);
        Intent intent = new Intent(mainActivity, LoginActivity.class);
        intent.putExtra(Keys.LOGIN_SCREEN_CALLED_FROM_LOGOUT, 1);
        mainActivity.startActivity(intent);
        mainActivity.finish();
        alertDialog.dismiss();
        Log.d(TAG, "This is after dialog dismiss");
        destroy();
    }
}
