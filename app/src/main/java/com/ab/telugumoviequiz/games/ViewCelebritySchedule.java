package com.ab.telugumoviequiz.games;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.Utils;

import java.util.ArrayList;
import java.util.List;

public class ViewCelebritySchedule extends DialogFragment implements View.OnClickListener {

    private final Context context;
    private final CelebrityFullDetails celebrityFullDetails;
    private Spinner namesSpinner;
    private final List<TextView> gameTimeTextViews = new ArrayList<>(24);
    private final List<Spinner> gameCelebritiesSpinners = new ArrayList<>(24);
    private final int normalColor = Color.parseColor("black");
    private final int selectColor = Color.parseColor("red");
    private int lastSelectedIndex = -1;

    public ViewCelebritySchedule(Context context, CelebrityFullDetails celebrityFullDetails) {
        this.context = context;
        this.celebrityFullDetails = celebrityFullDetails;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getDialog() != null) {
            getDialog().setTitle("Upcoming Celebrities Game Times");
        }

        View root = inflater.inflate(R.layout.view_celebrity_schedule, container, false);

        namesSpinner = root.findViewById(R.id.celebritySpinner);

        Button searchButton = root.findViewById(R.id.searchBut);
        searchButton.setOnClickListener(this);

        TableLayout tableLayout = root.findViewById(R.id.tableInvoices);

        Button closeButton = root.findViewById(R.id.user_answers_close_but);
        closeButton.setOnClickListener(this);

        int leftRowMargin = 0;
        int topRowMargin = 5;
        int rightRowMargin = 0;
        int bottomRowMargin = 5;

        int textSize = 50, smallTextSize = 40;

        ArrayAdapter<String> celebritiesSpinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                celebrityFullDetails.getMasterNames());
        celebritiesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        celebritiesSpinnerAdapter.setNotifyOnChange(false);
        namesSpinner.setAdapter(celebritiesSpinnerAdapter);

        List<UpcomingCelebrity> namesList = celebrityFullDetails.getNamesList();
        int rows = namesList.size();
        tableLayout.removeAllViews();

        Resources resources = getResources();
        String heading1 = resources.getString(R.string.col1);
        String heading2 = resources.getString(R.string.col2);

        for(int i = -1; i < rows; i ++) {
            UpcomingCelebrity row = null;
            if (i > -1) {
                row = namesList.get(i);
            }
            // data columns
            final TextView tv = new TextView(context);
            tv.setFocusable(true);
            tv.setFocusableInTouchMode(true);
            tv.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            //tv.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv.setText(heading1);
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            }
            else {
                tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setText(row.getGameStartTime());
                tv.setTextColor(normalColor);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                gameTimeTextViews.add(tv);
            }

            final Spinner spinnerComp = new Spinner(context);
            final TextView tv2 = new TextView(context);
            if (i == -1) {
                tv2.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv2.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv2.setText(heading2);
            }
            else {
                spinnerComp.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                spinnerComp.setGravity(Gravity.CENTER);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                        row.getCelebrityNames());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adapter.setNotifyOnChange(false);
                spinnerComp.setAdapter(adapter);
                gameCelebritiesSpinners.add(spinnerComp);
            }

            // add table row
            final TableRow tr = new TableRow(context);
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams = new
                    TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                    bottomRowMargin);
            tr.setPadding(0,0,0,0);
            tr.setLayoutParams(trParams);

            tr.addView(tv);
            if (i == -1) {
                tr.addView(tv2);
            } else {
                tr.addView(spinnerComp);
            }
            tableLayout.addView(tr, trParams);

            if (i > -1) {
                // add separator row
                final TableRow trSep = new TableRow(context);
                TableLayout.LayoutParams trParamsSep = new
                        TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trParamsSep.setMargins(leftRowMargin, topRowMargin,
                        rightRowMargin, bottomRowMargin);
                trSep.setLayoutParams(trParamsSep);
                TextView tvSep = new TextView(context);
                TableRow.LayoutParams tvSepLay = new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                tvSepLay.span = 2;
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep.setHeight(1);
                trSep.addView(tvSep);
                tableLayout.addView(trSep, trParamsSep);
            }
        }
        return root;
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
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.user_answers_close_but) {
            dismiss();
        } else if (view.getId() == R.id.searchBut) {
            if (lastSelectedIndex > -1) {
                TextView textView = gameTimeTextViews.get(lastSelectedIndex);
                Spinner spinner = gameCelebritiesSpinners.get(lastSelectedIndex);
                textView.setTextColor(normalColor);
                spinner.setSelection(0);
            }
            String selectedCelebrityName = (String) namesSpinner.getSelectedItem();
            String nextGameTime = null;
            int index = -1;
            int spinnerIndex = -1;
            for (UpcomingCelebrity uc : celebrityFullDetails.getNamesList()) {
                ++index;
                if (uc.getCelebrityNames() != null) {
                    if (uc.getCelebrityNames().contains(selectedCelebrityName)) {
                        nextGameTime = uc.getGameStartTime();
                        spinnerIndex = uc.getCelebrityNames().indexOf(selectedCelebrityName);
                        break;
                    }
                }
            }
            String infoMsg = "No Game Scheduled for " + selectedCelebrityName;
            if (nextGameTime != null) {
                infoMsg = "Next Game coming for " + selectedCelebrityName + "at :\n" + nextGameTime;
                TextView textView = gameTimeTextViews.get(index);
                Spinner spinner = gameCelebritiesSpinners.get(index);
                //textView.setTextColor(Color.parseColor("#FF0000"));
                textView.setTextColor(selectColor);
                spinner.setSelection(spinnerIndex, true);
                textView.requestFocus();
            }
            Utils.showMessage("", infoMsg, context, null);
        }
    }

    @Override
    public void onDismiss (@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        gameTimeTextViews.clear();
        gameCelebritiesSpinners.clear();
    }
}
