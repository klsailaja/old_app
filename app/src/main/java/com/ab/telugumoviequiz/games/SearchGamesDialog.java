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
    private List<String> gameIds, celebrityNames;
    private Spinner searchColsSpinner;
    private Spinner searchValsSpinner;
    private String[] searchCols;
    private ArrayAdapter<String> gameIdsAdapter, celebrityNamesAdapter;
    private MessageListener listener;

    public SearchGamesDialog(int gameMode) {
        this.gameMode = gameMode;
    }
    public void setData(List<String> gameIds, List<String> celebrityNames) {
        this.gameIds = gameIds;
        this.celebrityNames = celebrityNames;
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
            String[] keys = new String[1];
            keys[0] = searchCols[0];
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, searchCols);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchColsSpinner.setAdapter(adapter);
        searchColsSpinner.setSelection(0);

        gameIdsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, gameIds);
        gameIdsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameIdsAdapter.setNotifyOnChange(false);
        searchValsSpinner.setAdapter(gameIdsAdapter);
        searchValsSpinner.setSelection(0);

        celebrityNamesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, celebrityNames);
        celebrityNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        celebrityNamesAdapter.setNotifyOnChange(false);

        searchColsSpinner.setOnItemSelectedListener(this);
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
        if (selectedGameType == 0) {
            searchValsSpinner.setAdapter(gameIdsAdapter);
        } else {
            searchValsSpinner.setAdapter(celebrityNamesAdapter);
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
