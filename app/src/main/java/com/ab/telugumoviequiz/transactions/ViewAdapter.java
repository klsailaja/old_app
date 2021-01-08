package com.ab.telugumoviequiz.transactions;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.MyViewHolder> {

    private final List<MyTransaction> myTransactions;
    private final String[] headings;
    static int screenWidth;
    static int w1, w2, w3, w4, w5, w6, w7, w8;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
    private int creditColor = Color.parseColor("green");
    private int withdrawColor = Color.parseColor("red");

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView snoView , OBView, accTypeView, amtView, dateView, resultView, comentsView, CBView;

        MyViewHolder(View view) {
            super(view);
            snoView = view.findViewById(R.id.col1);
            OBView = view.findViewById(R.id.col2);
            accTypeView = view.findViewById(R.id.col3);
            amtView = view.findViewById(R.id.col4);
            dateView = view.findViewById(R.id.col5);
            resultView = view.findViewById(R.id.col6);
            CBView = view.findViewById(R.id.col7);
            comentsView = view.findViewById(R.id.col8);
        }
    }

    public ViewAdapter(List<MyTransaction> transactions, String[] headings) {
        this.myTransactions = transactions;
        this.headings = headings;
        w1 = (screenWidth * 6)/100;
        w2 = (screenWidth * 10)/100;
        w3 = (screenWidth * 12)/100;
        w4 = (screenWidth * 10)/100;
        w5 = (screenWidth * 15)/100;
        w6 = (screenWidth * 12)/100;
        w7 = (screenWidth * 10)/100;
        w8 = (screenWidth * 25)/100;
    }

    @NonNull
    @Override
    public ViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myreferral_table_item, parent, false);
        return new ViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewAdapter.MyViewHolder holder, int position) {

        holder.snoView.getLayoutParams().width = w1;
        holder.snoView.getLayoutParams().height = 200;
        holder.OBView.getLayoutParams().width = w2;
        holder.OBView.getLayoutParams().height = 200;
        holder.accTypeView.getLayoutParams().width = w3;
        holder.accTypeView.getLayoutParams().height = 200;
        holder.amtView.getLayoutParams().width = w4;
        holder.amtView.getLayoutParams().height = 200;
        holder.dateView.getLayoutParams().width = w5;
        holder.dateView.getLayoutParams().height = 200;
        holder.resultView.getLayoutParams().width = w6;
        holder.resultView.getLayoutParams().height = 200;
        holder.CBView.getLayoutParams().width = w7;
        holder.CBView.getLayoutParams().height = 200;
        holder.comentsView.getLayoutParams().width = w8;
        holder.comentsView.getLayoutParams().height = 200;

        /*
        holder.snoView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        holder.OBView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        holder.accTypeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        holder.amtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        holder.dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        holder.resultView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        holder.CBView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
         */



        if (position == 0) {
            holder.snoView.setText(headings[0]);
            holder.OBView.setText(headings[1]);
            holder.accTypeView.setText(headings[2]);
            holder.amtView.setText(headings[3]);
            holder.dateView.setText(headings[4]);
            holder.resultView.setText(headings[5]);
            holder.CBView.setText(headings[6]);
            holder.comentsView.setText(headings[7]);

            holder.snoView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.OBView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.accTypeView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.amtView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.dateView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.resultView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.comentsView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.CBView.setBackgroundResource(R.drawable.table_header_cell_bg);

        } else {
            MyTransaction myTransaction = myTransactions.get(position - 1);

            holder.snoView.setText(String.valueOf(myTransaction.getsNo()));
            holder.OBView.setText(String.valueOf(myTransaction.getOpeningBalance()));

            String accTypeName = "Main";
            if (myTransaction.getAccountType() == 2) {
                accTypeName = "Winning";
            } else if (myTransaction.getAccountType() == 3) {
                accTypeName = "Referral";
            }
            holder.accTypeView.setText(accTypeName);

            int signVal = -1;
            if ((myTransaction.getTransactionType() == 1) || (myTransaction.getTransactionType() == 3)
                    || (myTransaction.getTransactionType() == 5)) {
                signVal = 1; // +
            } else if ((myTransaction.getTransactionType() == 2) || (myTransaction.getTransactionType() == 4)
                    || (myTransaction.getTransactionType() == 6)) {
                signVal = -1; // -
            }

            int finalAmt = signVal * myTransaction.getAmount();
            String amtStrVal = String.valueOf(finalAmt);
            String signChar = "+";
            int textColor =  creditColor;
            if (finalAmt < 0) {
                signChar = "-";
                textColor = withdrawColor;
            }
            amtStrVal = signChar + amtStrVal;
            holder.amtView.setText(amtStrVal);
            holder.amtView.setTextColor(textColor);

            Date date = new Date(myTransaction.getDate());
            String datePattern = "dd:MMM:yyyy:HH:mm";
            simpleDateFormat.applyPattern(datePattern);
            String timeStr = simpleDateFormat.format(date);
            holder.dateView.setText(timeStr);

            String operResult = "Fail";
            textColor = withdrawColor;
            if (myTransaction.getOperResult() == 1) {
                operResult = "Success";
                textColor = creditColor;
            }
            holder.resultView.setText(operResult);
            holder.resultView.setTextColor(textColor);
            holder.comentsView.setText(myTransaction.getComment());
            //holder.comentsView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            holder.CBView.setText(String.valueOf(myTransaction.getClosingBalance()));

            holder.snoView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.OBView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.accTypeView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.amtView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.dateView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.resultView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.comentsView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.CBView.setBackgroundResource(R.drawable.table_content_cell_bg);
        }
    }

    @Override
    public int getItemCount() {
        return myTransactions.size() + 1;
    }
}
