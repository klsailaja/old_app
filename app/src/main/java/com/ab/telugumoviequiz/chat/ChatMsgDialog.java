package com.ab.telugumoviequiz.chat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.Utils;

import java.util.List;

public class ChatMsgDialog extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private final int messageType;
    public static int REQUEST = 1;
    public static int REPLY = 2;
    //public static int MIXED_GAME_TYPE = 0;
    public static int CELEBRITY_GAME_TYPE = 1;
    private List<String> mixGameTktRates, mixGameStartTimes, mixGameIds;
    private List<String> celebrityGameTktRates, celebrityGameStartTimes, celebrityGameIds;
    private ChatListener listener;

    private String[] gameTypeValues;
    private Spinner gameTypeSpinner, rateSpinner, timeSpinner, gameIdSpinner;
    private ArrayAdapter<String> mixRatesAdapter, mixStartTimesAdapter, mixIdsAdapter;
    private ArrayAdapter<String> celebRatesAdapter, celebStartTimesAdapter, celebIdsAdapter;

    public ChatMsgDialog(int messageType) {
        this.messageType = messageType;
    }

    public void setMixTypeData(List<String> mixGameTktRates, List<String> mixGameStartTimes, List<String> mixGameIds) {
        this.mixGameTktRates = mixGameTktRates;
        this.mixGameStartTimes = mixGameStartTimes;
        this.mixGameIds = mixGameIds;
    }
    public void setCelebrityTypeData(List<String> celebrityGameTktRates, List<String> celebrityGameStartTimes,
                                     List<String> celebrityGameIds) {
        this.celebrityGameTktRates = celebrityGameTktRates;
        this.celebrityGameStartTimes = celebrityGameStartTimes;
        this.celebrityGameIds = celebrityGameIds;
    }
    public void setChatListener(ChatListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Resources resources = getResources();
        gameTypeValues = resources.getStringArray(R.array.chat_game_types);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.chat_msg, container, false);

        Button closeButton = root.findViewById(R.id.user_answers_close_but);
        closeButton.setOnClickListener(this);

        gameTypeSpinner = root.findViewById(R.id.gameTypeSpinner);
        rateSpinner = root.findViewById(R.id.rateSpinner);
        timeSpinner = root.findViewById(R.id.gameTimeSpinner);
        gameIdSpinner = root.findViewById(R.id.gameIdSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, gameTypeValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameTypeSpinner.setAdapter(adapter);
        gameTypeSpinner.setSelection(0);

        mixRatesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mixGameTktRates);
        mixRatesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mixRatesAdapter.setNotifyOnChange(false);
        rateSpinner.setAdapter(mixRatesAdapter);
        rateSpinner.setSelection(0);

        mixStartTimesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mixGameStartTimes);
        mixStartTimesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mixStartTimesAdapter.setNotifyOnChange(false);
        timeSpinner.setAdapter(mixStartTimesAdapter);
        timeSpinner.setSelection(0);

        mixIdsAdapter  = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mixGameIds);
        mixIdsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mixIdsAdapter.setNotifyOnChange(false);
        gameIdSpinner.setAdapter(mixIdsAdapter);
        gameIdSpinner.setSelection(0);

        celebRatesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, celebrityGameTktRates);
        celebRatesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        celebRatesAdapter.setNotifyOnChange(false);

        celebStartTimesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, celebrityGameStartTimes);
        celebStartTimesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        celebStartTimesAdapter.setNotifyOnChange(false);

        celebIdsAdapter  = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, celebrityGameIds);
        celebIdsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        celebIdsAdapter.setNotifyOnChange(false);

        gameTypeSpinner.setOnItemSelectedListener(this);
        return root;
    }

    private void populateUI() {
        int selectedGameType = gameTypeSpinner.getSelectedItemPosition();
        if (selectedGameType == 0) {
            rateSpinner.setAdapter(mixRatesAdapter);
            timeSpinner.setAdapter(mixStartTimesAdapter);
            gameIdSpinner.setAdapter(mixIdsAdapter);
        } else {
            rateSpinner.setAdapter(celebRatesAdapter);
            timeSpinner.setAdapter(celebStartTimesAdapter);
            gameIdSpinner.setAdapter(celebIdsAdapter);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        populateUI();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onResume() {
        // Sets the height and the width of the DialogFragment
        super.onResume();
        int[] points = Utils.getScreenWidth(getContext());
        int width = (points[0] * 3) /4;
        //int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = (points[1] * 3/ 4);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(width, height);
            }
        }
        if (getDialog() != null) {
            getDialog().setTitle("Data is valid for short time only as time is running");
        }
    }

    @Override
    public void onClick(View view) {
        int gameType = gameTypeSpinner.getSelectedItemPosition();
        String gameRate = (String) rateSpinner.getSelectedItem();
        String gameTime = (String) timeSpinner.getSelectedItem();
        String gameId = (String) gameIdSpinner.getSelectedItem();
        listener.itemsSelected(messageType, gameType, gameRate, gameTime, gameId);
        dismiss();
    }

    @Override
    public void onDismiss (@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
