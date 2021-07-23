package com.ab.telugumoviequiz.games;

import android.app.Dialog;
import android.content.Context;
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

import androidx.fragment.app.DialogFragment;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.BlinkHandler;
import com.ab.telugumoviequiz.common.Utils;

import java.util.ArrayList;
import java.util.List;

public class ViewMyAnswers extends DialogFragment implements View.OnClickListener {
    private final List<UserAnswer> list;
    private final Context context;
    private BlinkHandler blinkHandler;
    private final String title;

    public ViewMyAnswers(Context context, List<UserAnswer> list, String title) {
        this.context = context;
        this.list = list;
        this.title = title;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.user_answers, container, false);
        TableLayout tableLayout = root.findViewById(R.id.tableInvoices);
        Button closeButton = root.findViewById(R.id.user_answers_close_but);
        closeButton.setOnClickListener(this);
        int correctCount = 0;
        long totalTimeTaken = 0;
        for (UserAnswer userAnswer : list) {
            if (userAnswer.isCorrect()) {
                correctCount++;
                totalTimeTaken = totalTimeTaken + userAnswer.getTimeTaken();
            }
        }
        Resources resources = getResources();
        String countStr = resources.getString(R.string.view_user_correct_ques_summary);
        StringBuilder stringBuilder = new StringBuilder(countStr);
        stringBuilder.append(" ");
        stringBuilder.append(correctCount);
        countStr = stringBuilder.toString();

        String totalTimeStr = resources.getString(R.string.view_user_correct_ques_time_taken);
        stringBuilder = new StringBuilder(totalTimeStr);
        stringBuilder.append(" ");
        String timeDefaultval = "-";
        String timeUserNotionStr = Utils.getUserNotionTimeStr(totalTimeTaken, true);
        if (timeUserNotionStr != null) {
            timeDefaultval = timeUserNotionStr;
        }
        stringBuilder.append(timeDefaultval);
        totalTimeStr = stringBuilder.toString();

        int leftRowMargin=0;
        int topRowMargin=0;
        int rightRowMargin=0;
        int bottomRowMargin = 0;

        int textSize = 50, smallTextSize = 50;
        int rows = list.size();
        tableLayout.removeAllViews();

        String heading1 = resources.getString(R.string.view_user_answers_Qno);
        String heading2 = resources.getString(R.string.view_user_answers_correctness);
        String heading3 = resources.getString(R.string.view_user_answers_timetaken);

        String notAnswered = resources.getString(R.string.view_user_answers_not_answered);
        String correctAns = resources.getString(R.string.view_user_answers_correct);
        String wrongAns = resources.getString(R.string.view_user_answers_wrong);
        String notApplicableTimeStr = resources.getString(R.string.view_user_answers_wrong_time);

        int tableHeaderBGColor = Color.parseColor("black");
        int tableHeaderFGCoor = Color.parseColor("white");

        int cellValuesBGColor = Color.parseColor("black");
        int cellValueCorrectFGColor = Color.parseColor("green");
        int cellValueWrongFGColor = Color.parseColor("black");
        int cellValueFGColor = Color.parseColor("white");


        for(int i = -1; i < rows; i ++) {
            UserAnswer row = null;
            if (i > -1)
                row = list.get(i);

            // data columns
            final TextView tv = new TextView(context);
            tv.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.START);
            //tv.setPadding(5, 15, 0, 15);

            int answerState = 0; // 0 - Not answered, 1 - wrong, 2 - right
            String userAnswerStr = notAnswered;
            String userAnsTimeStr = notApplicableTimeStr;
            String qNostr = "1";

            if (row != null) {
                qNostr = String.valueOf(row.getqNo());
                if (row.getTimeTaken() > 0) {
                    if (row.isCorrect()) {
                        answerState = 2;
                        userAnswerStr = correctAns;
                        userAnsTimeStr = Utils.getUserNotionTimeStr(row.getTimeTaken(), false);
                    } else {
                        answerState = 1;
                        userAnswerStr = wrongAns;
                    }
                }
            }
            if (i == -1) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv.setText(heading1);
                tv.setBackgroundColor(tableHeaderBGColor);
                tv.setTextColor(tableHeaderFGCoor);
            }
            else {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv.setText(qNostr);
                tv.setBackgroundColor(cellValuesBGColor);
                if (answerState <= 1) {
                    tv.setTextColor(cellValueWrongFGColor);
                } else {
                    tv.setTextColor(cellValueCorrectFGColor);
                }
            }
            final TextView tv2 = new TextView(context);
            tv2.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            if (i == -1) {
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv2.setBackgroundColor(tableHeaderBGColor);
                tv2.setTextColor(cellValueFGColor);
                tv2.setText(heading2);
            }
            else {
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv2.setBackgroundColor(cellValuesBGColor);
                if (answerState <= 1) {
                    tv2.setTextColor(cellValueWrongFGColor);
                } else {
                    tv2.setTextColor(cellValueCorrectFGColor);
                }
                tv2.setText(userAnswerStr);
            }
            tv2.setGravity(Gravity.START);
            //tv2.setPadding(5, 15, 0, 15);

            final TextView tv3 = new TextView(context);
            tv3.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            if (i == -1) {
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv3.setBackgroundColor(tableHeaderBGColor);
                tv3.setTextColor(tableHeaderFGCoor);
                tv3.setText(heading3);
            }
            else {
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv3.setBackgroundColor(cellValuesBGColor);
                if (answerState <= 1) {
                    tv3.setTextColor(cellValueWrongFGColor);
                } else {
                    tv3.setTextColor(cellValueCorrectFGColor);
                }
                tv3.setText(userAnsTimeStr);
            }
            tv3.setGravity(Gravity.START);
            //tv3.setPadding(5, 15, 0, 15);

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
                tvSepLay.span = 3;
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep.setHeight(1);
                trSep.addView(tvSep);
                tableLayout.addView(trSep, trParamsSep);
            }
        }

        final TextView summary1 = new TextView(context);
        summary1.setFocusable(true);
        summary1.setFocusableInTouchMode(true);
        TableRow.LayoutParams summary1Params = new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        summary1Params.span = 2;
        summary1.setLayoutParams(summary1Params);
        summary1.setGravity(Gravity.START);
        summary1.setText(countStr);
        summary1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        summary1.setTextColor(cellValueCorrectFGColor);


        final TextView summary2 = new TextView(context);
        summary2.setFocusable(true);
        summary2.setFocusableInTouchMode(true);
        TableRow.LayoutParams summary2Params = new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        summary2Params.span = 1;
        summary2.setLayoutParams(summary2Params);
        summary2.setGravity(Gravity.START);
        summary2.setText(totalTimeStr);
        summary2.setTextColor(cellValueCorrectFGColor);
        summary2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        final TableRow tr = new TableRow(context);
        tr.setId(10 + 1);
        TableLayout.LayoutParams trParams = new
                TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                bottomRowMargin);
        tr.setPadding(0,0,0,0);
        tr.setLayoutParams(trParams);
        tr.addView(summary1);
        tr.addView(summary2);
        tableLayout.addView(tr, trParams);

        List<TextView> blinkTextViews = new ArrayList<>();
        blinkTextViews.add(summary1);
        blinkTextViews.add(summary2);
        blinkHandler = new BlinkHandler(context, blinkTextViews);
        summary1.requestFocus();
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
                window.setTitle(title);
            }
        }
    }
    @Override
    public void onClick(View view) {
        blinkHandler.stop();
        dismiss();
    }
}
