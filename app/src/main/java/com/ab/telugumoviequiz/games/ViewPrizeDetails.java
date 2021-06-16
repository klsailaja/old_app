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
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;


import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.Utils;

import java.util.ArrayList;
import java.util.List;

public class ViewPrizeDetails extends DialogFragment implements View.OnClickListener {
    private List<PrizeDetail> list = new ArrayList<>();
    private final Context context;

    public ViewPrizeDetails(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle data = getArguments();
        if (data != null) {
            list = data.getParcelableArrayList("PrizeDetails");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.user_answers, container, false);
        TableLayout tableLayout = root.findViewById(R.id.tableInvoices);
        Button closeButton = root.findViewById(R.id.user_answers_close_but);
        closeButton.setOnClickListener(this);

        int leftRowMargin=0;
        int topRowMargin=0;
        int rightRowMargin=0;
        int bottomRowMargin = 0;

        int textSize = 50, smallTextSize = 40;
        int rows = list.size();
        tableLayout.removeAllViews();
        Resources resources = getResources();
        String heading1 = resources.getString(R.string.view_prize_details_rank);
        String heading2 = resources.getString(R.string.view_prize_details_amt);
        String freeGamePrizeMoney = resources.getString(R.string.view_prize_details_free_game_prize);
        for(int i = -1; i < rows; i ++) {
            PrizeDetail row = null;
            if (i > -1)
                row = list.get(i);

            // data columns
            final TextView tv = new TextView(context);
            tv.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.START);
            tv.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv.setText(heading1);
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            }
            else {
                tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setText(String.valueOf(row.getRank()));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            final TextView tv2 = new TextView(context);
            if (i == -1) {
                tv2.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            }
            else {
                tv2.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            tv2.setGravity(Gravity.START);
            tv2.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv2.setText(heading2);
                tv2.setBackgroundColor(Color.parseColor("#f7f7f7"));
            }
            else {
                tv2.setBackgroundColor(Color.parseColor("#ffffff"));
                tv2.setTextColor(Color.parseColor("#000000"));
                if (row.getPrizeMoney() == 0) {
                    tv2.setText(freeGamePrizeMoney);
                } else {
                    tv2.setText(String.valueOf(row.getPrizeMoney()));
                }
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
            tr.addView(tv2);
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
        int width = (points[0] * 100) /100;
        //int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = (points[1] * 95) /100;
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
        dismiss();
    }

    @Override
    public void onDismiss (@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
