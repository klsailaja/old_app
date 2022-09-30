package com.ab.telugumoviequiz.main;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.DialogAction;
import com.ab.telugumoviequiz.common.Utils;

import java.util.ArrayList;
import java.util.List;

public class ServerErrorHandler implements DialogAction {
    public static int APP_SHUTDOWN = -90;
    private List<DialogAction> shutDownListeners = new ArrayList<>();
    private Activity mainActivity;
    private static ServerErrorHandler instance;
    private boolean showing = false;

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
        if (!shutDownListeners.contains(listener)) {
            shutDownListeners.add(listener);
        }
    }
    public void removeShutdownListener(DialogAction listener) {
        shutDownListeners.remove(listener);
    }
    private void notifyListeners() {
        for (DialogAction listener : shutDownListeners) {
            try {
                listener.doAction(APP_SHUTDOWN, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void doAction(int calledId, Object userObject) {
        AlertDialog alertDialog = Utils.getProgressDialog(mainActivity, Utils.WAIT_MESSAGE);
        alertDialog.show();
        notifyListeners();
        Intent intent = new Intent(mainActivity, LoginActivity.class);
        mainActivity.finish();
        mainActivity.startActivity(intent);
        alertDialog.dismiss();
        mainActivity = null;
        destroy();
        Utils.shutdown(mainActivity.getResources().getString(R.string.base_url));
    }
}
