package com.ab.telugumoviequiz.common;

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public abstract class BaseFragment extends Fragment {

    public BaseFragment() {
    }

    public boolean handleServerError(boolean exceptionThrown, boolean isAPIException, final Object response) {
        if ((exceptionThrown) && (!isAPIException)) {
            showErr("Check your Internet Connectivity");
            return true;
        }
        return false;
    }

    public boolean handleAPIError(boolean isAPIException, final Object response) {
        if (isAPIException) {
            showErr("Server Problem. Please retry after some time." + (String)response);
            return true;
        }
        return false;
    }

    public void showErrShowHomeScreen(final String errMsg) {
        final Activity parentActvity = getActivity();
        Runnable run = () -> Utils.showMessage("Error", errMsg, getContext(), new ShowHomeScreen(parentActvity));
        if (parentActvity != null) {
            parentActvity.runOnUiThread(run);
        }
    }

    public void showErr(final String errMsg) {
        final Activity parentActvity = getActivity();
        Runnable run = () -> Utils.showMessage("Error", errMsg, getContext(), null);
        if (parentActvity != null) {
            parentActvity.runOnUiThread(run);
        }
    }

    public void showSnackBarMessage(final String msg) {
        final Activity parentActivity = getActivity();
        Runnable run = () -> { Snackbar snackbar = Snackbar.make(getSnackBarComponent(), msg, Snackbar.LENGTH_SHORT);
            snackbar.show(); };
        if (parentActivity != null) {
            parentActivity.runOnUiThread(run);
        }
    }

    public View getSnackBarComponent() {
        return null;
    }
}
