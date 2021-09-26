package com.ab.telugumoviequiz.games;

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
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.MessageListener;
import com.ab.telugumoviequiz.common.Utils;

import java.util.ArrayList;
import java.util.List;

public class SearchGamesDialog extends DialogFragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private final int gameMode;
    private List<String> gameIds, celebrityNames, gameTimes, gameRates;
    private Spinner searchColsSpinner;
    private Spinner searchValsSpinner;
    private CheckBox box;
    private String[] searchCols;
    private ArrayAdapter<String> gameIdsAdapter, celebrityNamesAdapter, gameTimesAdapter, gameRatesAdapter;
    private MessageListener listener;

    public SearchGamesDialog(int gameMode) {
        this.gameMode = gameMode;
    }
    public void setData(List<String> gameIds, List<String> celebrityNames,
                        List<String> gameTimes, List<String> gameRates) {
        this.gameIds = gameIds;
        this.celebrityNames = celebrityNames;
        this.gameTimes = gameTimes;
        this.gameRates = gameRates;
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Resources resources = getResources();
        searchCols = resources.getStringArray(R.array.search_options);
        if (gameMode == 1) {
            String[] keys = new String[3];
            keys[0] = searchCols[0];
            keys[1] = searchCols[1];
            keys[2] = searchCols[2];
            searchCols = keys;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.search_dialog, container, false);

        Button searchButton = root.findViewById(R.id.search_but);
        searchButton.setOnClickListener(this);

        Button clearButton = root.findViewById(R.id.clear_but);
        clearButton.setOnClickListener(this);

        Button closeButton = root.findViewById(R.id.close_but);
        closeButton.setOnClickListener(this);

        searchColsSpinner = root.findViewById(R.id.searchCols);
        searchValsSpinner = root.findViewById(R.id.searchVals);
        searchColsSpinner.getLayoutParams().height = 200;

        box = root.findViewById(R.id.showNextFree);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_list_item, searchCols);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchColsSpinner.setAdapter(adapter);

        gameIdsAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_list_item, gameIds);
        gameIdsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameIdsAdapter.setNotifyOnChange(false);
        searchValsSpinner.setAdapter(gameIdsAdapter);
        searchValsSpinner.setSelection(0);

        celebrityNamesAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_list_item, celebrityNames);
        celebrityNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        celebrityNamesAdapter.setNotifyOnChange(false);

        gameTimesAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_list_item, gameTimes);
        gameTimesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameTimesAdapter.setNotifyOnChange(false);

        gameRatesAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_list_item, gameRates);
        gameRatesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameRatesAdapter.setNotifyOnChange(false);

        searchColsSpinner.setOnItemSelectedListener(this);

        searchColsSpinner.setSelection(2);
        box.setSelected(true);
        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        populateUI();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    private void populateUI() {
        int selectedGameType = searchColsSpinner.getSelectedItemPosition();
        box.setEnabled(false);
        if (selectedGameType == 0) {
            searchValsSpinner.setAdapter(gameIdsAdapter);
        } else if (selectedGameType == 1) {
            box.setEnabled(true);
            searchValsSpinner.setAdapter(gameTimesAdapter);
        } else if (selectedGameType == 2) {
            box.setEnabled(true);
            searchValsSpinner.setAdapter(gameRatesAdapter);
        } else {
            searchValsSpinner.setAdapter(celebrityNamesAdapter);
            box.setEnabled(true);
        }
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
        if (view.getId() == R.id.search_but) {
            int index = searchColsSpinner.getSelectedItemPosition();
            String searchKey = searchCols[index];
            String searchValue = (String) searchValsSpinner.getSelectedItem();
            searchValue = searchValue.trim();
            if (searchValue.length() == 0) {
                Utils.showMessage("Error", "Please enter a value to search", getContext(), null);
                return;
            }
            List<String> searchVals = new ArrayList<>();
            searchVals.add(searchKey);
            searchVals.add(searchValue);
            if (box.isChecked()) {
                searchVals.add("true");
            } else {
                searchVals.add("false");
            }

            listener.passData(1, searchVals);
            dismiss();
        } else if (view.getId() == R.id.clear_but) {
            listener.passData(2, null);
            dismiss();
        } else if (view.getId() == R.id.close_but) {
            dismiss();
        }
    }

    @Override
    public void onDismiss (@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
