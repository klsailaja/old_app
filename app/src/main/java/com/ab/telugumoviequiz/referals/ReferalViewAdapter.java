package com.ab.telugumoviequiz.referals;

import android.annotation.SuppressLint;
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

public class ReferalViewAdapter extends RecyclerView.Adapter<ReferalViewAdapter.MyViewHolder> {

    private final List<UserReferal> userReferals;
    private final String[] headings;
    static int screenWidth;
    static int screenHeight;
    static int w1, w2, w3;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView snoView , userNameView, lastSeenView;

        MyViewHolder(View view) {
            super(view);
            snoView = view.findViewById(R.id.col1);
            userNameView = view.findViewById(R.id.col2);
            lastSeenView = view.findViewById(R.id.col3);

            TextView notNeeded = view.findViewById(R.id.col4);
            notNeeded.setVisibility(View.GONE);
            notNeeded = view.findViewById(R.id.col5);
            notNeeded.setVisibility(View.GONE);
            notNeeded = view.findViewById(R.id.col6);
            notNeeded.setVisibility(View.GONE);
            notNeeded = view.findViewById(R.id.col7);
            notNeeded.setVisibility(View.GONE);
            notNeeded = view.findViewById(R.id.col8);
            notNeeded.setVisibility(View.GONE);
        }
    }

    public ReferalViewAdapter(List<UserReferal> userReferals, String[] headings) {
        this.userReferals = userReferals;
        this.headings = headings;
        w1 = (screenWidth * 20)/100;
        w2 = (screenWidth * 50)/100;
        w3 = (screenWidth * 30)/100;
    }

    @NonNull
    @Override
    public ReferalViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.referal_list_item, parent, false);
        return new ReferalViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReferalViewAdapter.MyViewHolder holder, int position) {
        holder.snoView.getLayoutParams().width = w1;
        holder.userNameView.getLayoutParams().width = w2;
        holder.lastSeenView.getLayoutParams().width = w3;

        if (position == 0) {
            holder.snoView.setText(headings[0]);
            holder.userNameView.setText(headings[1]);
            holder.lastSeenView.setText(headings[2]);

            holder.snoView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.userNameView.setBackgroundResource(R.drawable.table_header_cell_bg);
            holder.lastSeenView.setBackgroundResource(R.drawable.table_header_cell_bg);

        } else {
            UserReferal userReferal = userReferals.get(position - 1);

            holder.snoView.setText(String.valueOf(userReferal.getSno()));
            //holder.snoView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            holder.userNameView.setText(userReferal.getUserName());
            //holder.userNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            Date date = new Date(userReferal.getLastLoggedDate());
            String datePattern = "dd:MMM:yyyy-HH:mm";
            simpleDateFormat.applyPattern(datePattern);
            String timeStr = simpleDateFormat.format(date);
            holder.lastSeenView.setText(timeStr);
            //holder.lastSeenView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            holder.snoView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.userNameView.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.lastSeenView.setBackgroundResource(R.drawable.table_content_cell_bg);
        }
    }
    @Override
    public int getItemCount() {
        return userReferals.size() + 1;
    }
}
