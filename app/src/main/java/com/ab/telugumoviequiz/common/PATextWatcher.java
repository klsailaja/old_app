package com.ab.telugumoviequiz.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by user on 10/13/2016.
 */

public class PATextWatcher implements TextWatcher {
    private final TextView mView;
    private final NotifyTextChanged mListener;

    public PATextWatcher(TextView view, NotifyTextChanged listener) {
        this.mView = view;
        this.mListener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        mListener.textChanged(mView.getId());
    }
}

