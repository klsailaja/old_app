package com.ab.telugumoviequiz.common;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.ab.telugumoviequiz.R;

public class PreferenceDialog extends DialogFragment
        implements View.OnClickListener {

    private final static String preference_file_key = "com.ab.telugumoviequiz.common.pref_file";

    public PreferenceDialog() {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.preferences, container, false);

        Switch soundSwitch = root.findViewById(R.id.soundSwitch);
        soundSwitch.setChecked(UserDetails.getInstance().getIsGameSoundOn());

        Switch notificationSwitch = root.findViewById(R.id.notificationSwitch);
        notificationSwitch.setChecked(UserDetails.getInstance().getNotificationValue());

        Button okButton = root.findViewById(R.id.pref_but);
        okButton.setOnClickListener(this);
        return root;
   }

    @Override
    public void onResume() {
        // Sets the height and the width of the DialogFragment
        super.onResume();
        int[] points = Utils.getScreenWidth(getContext());
        int width = (points[0] * 3) / 4;
        //int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = (points[1] * 3) / 4;
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(width, height);
            }
        }
    }

    @Override
    public void onClick(View view) {
        View rootView = getView();
        if (rootView != null) {
            Switch soundSwitch = rootView.findViewById(R.id.soundSwitch);
            UserDetails.getInstance().setIsGameSoundOn(soundSwitch.isChecked());

            Switch notificationSwitch = rootView.findViewById(R.id.notificationSwitch);
            UserDetails.getInstance().setNotificationValue(notificationSwitch.isChecked());
        }
        this.dismiss();
    }

    public static void readStateUpdateInMem(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                preference_file_key, Context.MODE_PRIVATE);

        boolean soundPreference = sharedPref.getBoolean(Keys.APP_SOUND_KEY, true);
        UserDetails.getInstance().setIsGameSoundOn(soundPreference);

        boolean notificationPreference = sharedPref.getBoolean(Keys.APP_NOTIFICATION_KEY, true);
        UserDetails.getInstance().setNotificationValue(notificationPreference);
    }

    public static void storeState(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                preference_file_key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Keys.APP_SOUND_KEY, UserDetails.getInstance().getIsGameSoundOn());
        editor.putBoolean(Keys.APP_NOTIFICATION_KEY, UserDetails.getInstance().getNotificationValue());
        editor.apply();
    }
}
