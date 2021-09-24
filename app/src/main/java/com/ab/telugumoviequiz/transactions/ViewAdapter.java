package com.ab.telugumoviequiz.transactions;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.ab.telugumoviequiz.transactions.TransactionsView.MORE_OPTIONS_BUTTON_ID;

public class ViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<MyTransaction> myTransactions;
    private final String[] headings;
    static int screenWidth;
    static int w1, w2, w3, w4, w5, w6, w7, w8;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
    private final int creditColor = Color.parseColor("#FF138C18");
    private final int withdrawColor = Color.parseColor("#FF0000");
    private View.OnClickListener mClickListener;
    private static final int HEADING_VIEW = 1;
    private static final int DATA_VIEW = 2;

    static class HeadViewHolder extends RecyclerView.ViewHolder {
        TextView snoView , OBView, transactionTypeView, amtView, dateView, comentsView, CBView, moreOptionsHeading;

        HeadViewHolder(View view) {
            super(view);
            snoView = view.findViewById(R.id.col1);
            transactionTypeView = view.findViewById(R.id.col2);
            OBView = view.findViewById(R.id.col3);
            amtView = view.findViewById(R.id.col4);
            CBView = view.findViewById(R.id.col5);
            dateView = view.findViewById(R.id.col6);
            comentsView = view.findViewById(R.id.col7);
            moreOptionsHeading = view.findViewById(R.id.col8);
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView snoView , OBView, transactionTypeView, amtView, dateView, comentsView, CBView;
        ImageButton moreOptionsButton;

        MyViewHolder(View view) {
            super(view);
            snoView = view.findViewById(R.id.col1);
            transactionTypeView = view.findViewById(R.id.col2);
            OBView = view.findViewById(R.id.col3);
            amtView = view.findViewById(R.id.col4);
            CBView = view.findViewById(R.id.col5);
            dateView = view.findViewById(R.id.col6);
            comentsView = view.findViewById(R.id.col7);
            moreOptionsButton = view.findViewById(R.id.col8);
        }
    }

    public ViewAdapter(List<MyTransaction> transactions, String[] headings) {
        this.myTransactions = transactions;
        this.headings = headings;
        w1 = (screenWidth * 6)/100;
        w2 = (screenWidth * 13)/100;
        w3 = (screenWidth * 12)/100;
        w4 = (screenWidth * 10)/100;
        w5 = (screenWidth * 12)/100;
        w6 = (screenWidth * 15)/100;
        w7 = (screenWidth * 23)/100;
        w8 = (screenWidth * 10)/100;
    }

    void setClickListener(View.OnClickListener listener) {
        this.mClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADING_VIEW) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mytransactions_table_item_head, parent, false);
            return new com.ab.telugumoviequiz.transactions.ViewAdapter.HeadViewHolder(itemView);
        }
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mytransactions_table_item, parent, false);
        return new ViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderArg, int position) {
        int cellHeight = 210;
        if (position == 0) {

            ViewAdapter.HeadViewHolder holder = (ViewAdapter.HeadViewHolder) holderArg;

            holder.snoView.getLayoutParams().width = w1;
            holder.snoView.getLayoutParams().height = cellHeight;
            holder.transactionTypeView.getLayoutParams().width = w2;
            holder.transactionTypeView.getLayoutParams().height = cellHeight;
            holder.OBView.getLayoutParams().width = w3;
            holder.OBView.getLayoutParams().height = cellHeight;
            holder.amtView.getLayoutParams().width = w4;
            holder.amtView.getLayoutParams().height = cellHeight;
            holder.CBView.getLayoutParams().width = w5;
            holder.CBView.getLayoutParams().height = cellHeight;
            holder.dateView.getLayoutParams().width = w6;
            holder.dateView.getLayoutParams().height = cellHeight;
            holder.comentsView.getLayoutParams().width = w7;
            holder.comentsView.getLayoutParams().height = cellHeight;
            holder.moreOptionsHeading.getLayoutParams().width = w8;
            holder.moreOptionsHeading.getLayoutParams().height = cellHeight;

            holder.snoView.setText(headings[0]);
            holder.transactionTypeView.setText(headings[1]);
            holder.OBView.setText(headings[2]);
            holder.amtView.setText(headings[3]);
            holder.CBView.setText(headings[4]);
            holder.dateView.setText(headings[5]);
            holder.comentsView.setText(headings[6]);
            holder.moreOptionsHeading.setText(headings[7]);

            holder.snoView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.transactionTypeView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.OBView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.amtView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.dateView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.CBView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.dateView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.comentsView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.moreOptionsHeading.setBackgroundResource(R.drawable.table_header_cell_bg);

        } else if (position > 0) {
            MyViewHolder holder = (MyViewHolder) holderArg;

            holder.snoView.getLayoutParams().width = w1;
            holder.snoView.getLayoutParams().height = cellHeight;
            holder.transactionTypeView.getLayoutParams().width = w2;
            holder.transactionTypeView.getLayoutParams().height = cellHeight;
            holder.OBView.getLayoutParams().width = w3;
            holder.OBView.getLayoutParams().height = cellHeight;
            holder.amtView.getLayoutParams().width = w4;
            holder.amtView.getLayoutParams().height = cellHeight;
            holder.CBView.getLayoutParams().width = w5;
            holder.CBView.getLayoutParams().height = cellHeight;
            holder.dateView.getLayoutParams().width = w6;
            holder.dateView.getLayoutParams().height = cellHeight;
            holder.comentsView.getLayoutParams().width = w7;
            holder.comentsView.getLayoutParams().height = cellHeight;
            holder.moreOptionsButton.getLayoutParams().width = w8;
            holder.moreOptionsButton.getLayoutParams().height = cellHeight + 10;

            MyTransaction myTransaction = myTransactions.get(position - 1);

            holder.snoView.setText(String.valueOf(myTransaction.getsNo()));
            holder.OBView.setText(String.valueOf(myTransaction.getOpeningBalance()));

            String accTypeName = "Main";
            if (myTransaction.getAccountType() == 2) {
                accTypeName = "Winning";
            } else if (myTransaction.getAccountType() == 3) {
                accTypeName = "Referral";
            }
            holder.transactionTypeView.setText(accTypeName);
            
            int signVal = -1;
            if ((myTransaction.getTransactionType() == 1) || (myTransaction.getTransactionType() == 4)
                    || (myTransaction.getTransactionType() == 5)) {
                signVal = 1; // +
            } /*else if ((myTransaction.getTransactionType() == 2) || (myTransaction.getTransactionType() == 3)
                    || (myTransaction.getTransactionType() == 6)) {
                signVal = -1;
            }*/

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
            String datePattern = "HH:mm-dd:MMM:yyyy";
            simpleDateFormat.applyPattern(datePattern);
            String timeStr = simpleDateFormat.format(date);
            holder.dateView.setText(timeStr);

            holder.comentsView.setText(myTransaction.getComment());
            holder.CBView.setText(String.valueOf(myTransaction.getClosingBalance()));

            holder.snoView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.OBView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.transactionTypeView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.amtView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.dateView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.comentsView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.CBView.setBackgroundResource(R.drawable.table_content_cell_bg);

            holder.moreOptionsButton.setId(MORE_OPTIONS_BUTTON_ID);
            holder.moreOptionsButton.setTag(myTransaction);
            holder.moreOptionsButton.setOnClickListener(mClickListener);
        }
    }

    @Override
    public int getItemViewType (int position) {
        if (position == 0) {
            return HEADING_VIEW;
        }
        return DATA_VIEW;
    }

    @Override
    public int getItemCount() {
        return myTransactions.size() + 1;
    }
}
