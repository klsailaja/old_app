package com.ab.telugumoviequiz.common;

import android.app.Activity;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    public BaseFragment() {
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
}
