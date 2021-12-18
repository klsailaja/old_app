package com.ab.telugumoviequiz.common;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private Activity activity;
    private DatePickerDialog.OnDateSetListener listener;

    public DatePickerFragment(Activity activity,
                              DatePickerDialog.OnDateSetListener listener) {
        this.activity = activity;
        this.listener = listener;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(activity, listener, year, month, day);
    }
}
