package com.ab.telugumoviequiz.common;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ab.telugumoviequiz.main.MainActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public abstract class BaseFragment extends Fragment implements MessageListener,
        NavigationView.OnNavigationItemSelectedListener {

    public BaseFragment() {
    }

    public boolean handleServerError(boolean exceptionThrown, boolean isAPIException, final Object response) {
        Activity parentActivity = getActivity();
        if (parentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) parentActivity;
            return mainActivity.handleServerError(exceptionThrown, isAPIException, response);
        }
        return false;
    }

    public boolean handleAPIError(boolean isAPIException, final Object response, int errorType,
                                  View view, DialogAction dialogAction) {
        Activity parentActivity = getActivity();
        if (parentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) parentActivity;
            return mainActivity.handleAPIError(isAPIException, response, errorType, view, dialogAction);
        }
        return false;
    }

    public void displayInfo(String infoMsg, DialogAction dialogAction) {
        Activity parentActivity = getActivity();
        if (parentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) parentActivity;
            mainActivity.displayInfo(infoMsg, dialogAction);
        }
    }

    public void displayError(String errMsg, DialogAction dialogAction) {
        Activity parentActivity = getActivity();
        if (parentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) parentActivity;
            mainActivity.displayError(errMsg, dialogAction);
        }
    }

    public void displayMsg(final String title, final String msg, DialogAction dialogAction) {
        Activity parentActivity = getActivity();
        if (parentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) parentActivity;
            mainActivity.displayMsg(title, msg, dialogAction);
        }
    }

    public void displayErrorAsToast(final String errMsg) {
        Activity parentActivity = getActivity();
        if (parentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) parentActivity;
            mainActivity.displayErrorAsToast(errMsg);
        }
    }
    public void displayErrorAsSnackBar(final String errMsg, View view) {
        Activity parentActivity = getActivity();
        if (parentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) parentActivity;
            mainActivity.displayErrorAsSnackBar(errMsg, view);
        }
    }

    public void showErrShowHomeScreen(final String errMsg) {
        final Activity parentActvity = getActivity();
        displayError(errMsg, new ShowHomeScreen(parentActvity));
    }

    @Override
    public void passData(int reqId, List<String> data) {
        String msg = data.get(0);
        Runnable run = () -> {
            TextView statusBar = getStatusBar();
            if (statusBar == null) {
                return;
            }
            statusBar.setText(msg);
        };
        Activity parentActivity = getActivity();
        if (parentActivity != null) {
            parentActivity.runOnUiThread(run);
        }
    }

    public TextView getStatusBar() {
        return null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Activity parentActivity = getActivity();
        if (parentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) parentActivity;
            return mainActivity.onNavigationItemSelected(item);
        }
        return false;
    }
}
