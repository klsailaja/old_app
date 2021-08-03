package com.ab.telugumoviequiz.money;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.Utils;

public class AddMoneyViewAdapter extends RecyclerView.Adapter<AddMoneyViewAdapter.MyViewHolder> {

    private final View.OnClickListener listener;
    private final int[] values = {100, 200, 500, 1000};

    public AddMoneyViewAdapter(View.OnClickListener listener) {
        this.listener = listener;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        Button moneyButton;

        MyViewHolder(View view) {
            super(view);
            CardView cardView = view.findViewById(R.id.gameCardEntry);
            cardView.getLayoutParams().width =  Utils.getScreenWidth(cardView.getContext())[0]/ 5;
            moneyButton = view.findViewById(R.id.list_money_button);
        }
    }

    @NonNull
    @Override
    public AddMoneyViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        int layoutId = R.layout.list_row_buttons;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new AddMoneyViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddMoneyViewAdapter.MyViewHolder holder, int position) {
        holder.moneyButton.setText("Add " + values[position] + " INR");
        holder.moneyButton.setOnClickListener(listener);
        holder.moneyButton.setTag(values[position]);
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
