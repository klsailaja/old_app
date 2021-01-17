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
import com.ab.telugumoviequiz.common.UserDetails;
import com.ab.telugumoviequiz.common.Utils;

import java.util.List;

public class ViewLeaderboard extends DialogFragment implements View.OnClickListener {
    private final boolean isGameOver;
    private final List<PlayerSummary> list;
    private final Context context;
    private boolean fromHistory;

    public ViewLeaderboard(Context context, boolean isGameOver, List<PlayerSummary> list) {
        this.context = context;
        this.isGameOver = isGameOver;
        this.list = list;
    }

    public ViewLeaderboard(Context context, boolean isGameOver, List<PlayerSummary> list, boolean fromHistory) {
        this.context = context;
        this.isGameOver = isGameOver;
        this.list = list;
        this.fromHistory = true;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!fromHistory) {
            if (getDialog() != null) {
                getDialog().setTitle("Closes automatically after 15 secs");
            }
        }
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
        String heading1 = resources.getString(R.string.view_leadership_rank);
        String heading2 = resources.getString(R.string.view_leadership_correct_count);
        String heading3 = resources.getString(R.string.view_leadership_time_taken);
        String heading4 = resources.getString(R.string.view_leadership_username);
        String heading5 = resources.getString(R.string.view_leadership_amount);
        String notAnswered = resources.getString(R.string.view_user_answers_not_answered);

        int highlightTextSizeRowOffset;
        TextView userTextView = null;
        for(int i = -1; i < rows; i ++) {
            PlayerSummary row = null;
            highlightTextSizeRowOffset = 0;
            if (i > -1) {
                row = list.get(i);
                if (isGameOver) {
                    if (row.getAmountWon() > 0) {
                        highlightTextSizeRowOffset = 10;
                    }
                } else {
                    if (row.getUserProfileId() == UserDetails.getInstance().getUserProfile().getId()) {
                        highlightTextSizeRowOffset = 10;
                    }
                }
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
                tv.setText(String.valueOf(row.getRank()));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                if (highlightTextSizeRowOffset == 10) {
                    tv.setTextColor(Color.parseColor("#FF0000"));
                    userTextView = tv;
                }
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
            tv2.setGravity(Gravity.CENTER);
            //tv2.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv2.setText(heading2);
                tv2.setBackgroundColor(Color.parseColor("#f7f7f7"));
            }
            else {
                tv2.setBackgroundColor(Color.parseColor("#ffffff"));
                tv2.setTextColor(Color.parseColor("#000000"));
                tv2.setText(String.valueOf(row.getCorrectCount()));
                if (highlightTextSizeRowOffset == 10) {
                    tv2.setTextColor(Color.parseColor("#FF0000"));
                }
            }

            final TextView tv3 = new TextView(context);
            if (i == -1) {
                tv3.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            }
            else {
                tv3.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            tv3.setGravity(Gravity.CENTER);
            //tv3.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv3.setText(heading3);
                tv3.setBackgroundColor(Color.parseColor("#f7f7f7"));
            }
            else {
                tv3.setBackgroundColor(Color.parseColor("#ffffff"));
                tv3.setTextColor(Color.parseColor("#000000"));
                String timeVal = notAnswered;
                String thirdCol = Utils.getUserNotionTimeStr(row.getTotalTime(), true);
                if (thirdCol != null) {
                    timeVal = thirdCol;
                }
                tv3.setText(timeVal);
                if (highlightTextSizeRowOffset == 10) {
                    tv3.setTextColor(Color.parseColor("#FF0000"));
                }
            }

            final TextView tv4 = new TextView(context);
            if (i == -1) {
                tv4.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            }
            else {
                tv4.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            tv4.setGravity(Gravity.CENTER);
            //tv4.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv4.setText(heading4);
                tv4.setBackgroundColor(Color.parseColor("#f7f7f7"));
            }
            else {
                tv4.setBackgroundColor(Color.parseColor("#ffffff"));
                tv4.setTextColor(Color.parseColor("#000000"));
                tv4.setText(String.valueOf(row.getUserName()));
                if (highlightTextSizeRowOffset == 10) {
                    tv4.setTextColor(Color.parseColor("#FF0000"));
                }
            }

            final TextView tv5 = new TextView(context);
            if (i == -1) {
                tv5.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv5.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            }
            else {
                tv5.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv5.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                if (highlightTextSizeRowOffset == 10) {
                    tv5.setTextColor(Color.parseColor("#FF0000"));
                }
            }
            tv5.setGravity(Gravity.CENTER);
            //tv5.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv5.setText(heading5);
                tv5.setBackgroundColor(Color.parseColor("#f7f7f7"));
            }
            else {
                tv5.setBackgroundColor(Color.parseColor("#ffffff"));
                tv5.setTextColor(Color.parseColor("#000000"));
                tv5.setText(String.valueOf(row.getAmountWon()));
                if (highlightTextSizeRowOffset == 10) {
                    tv5.setTextColor(Color.parseColor("#FF0000"));
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
            tr.addView(tv3);
            tr.addView(tv4);
            if (isGameOver) {
                tr.addView(tv5);
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
                tvSepLay.span = 4;
                if (isGameOver) {
                    tvSepLay.span = 5;
                }
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep.setHeight(1);
                trSep.addView(tvSep);
                tableLayout.addView(trSep, trParamsSep);
            }
        }
        if (!isGameOver) {
            if (userTextView != null) {
                userTextView.requestFocus();
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
        dismiss();
    }

    @Override
    public void onDismiss (@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
