package com.ab.telugumoviequiz.games;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.Utils;

import java.util.List;

public class PaymentOptionsAdapter extends RecyclerView.Adapter<PaymentOptionsAdapter.MyViewHolder> {

    private final List<PayGameModel> data;
    private final View.OnClickListener listener;
    int[] points;
    static int width;
    static int height;

    public PaymentOptionsAdapter(List<PayGameModel> data, View.OnClickListener listener, Context context) {
        this.data = data;
        this.listener = listener;
        points = Utils.getScreenWidth(context);
        width = (points[0] * 3)/4;
        height = (points[1] * 4)/4;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        Button accountTypeBut;
        TextView accountBalance;


        MyViewHolder(View view) {
            super(view);
            CardView cardView = view.findViewById(R.id.gameTypeCardEntry);
            cardView.getLayoutParams().width = width / 3;
            cardView.getLayoutParams().height = height / 2;

            accountTypeBut = cardView.findViewById(R.id.gameTypeEntryBut);
            accountTypeBut.getLayoutParams().width = width / 5;
            accountTypeBut.getLayoutParams().height = height / 3;
            accountBalance = cardView.findViewById(R.id.gameTypeEntryName);
        }
    }

    @NonNull
    @Override
    public PaymentOptionsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_type_entry, parent, false);
        return new PaymentOptionsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PaymentOptionsAdapter.MyViewHolder holder, int position) {
        PayGameModel model = data.get(position);
        holder.accountTypeBut.setText(model.getAccountName());
        holder.accountBalance.setText(model.getAccountBalance());
        holder.accountTypeBut.setOnClickListener(listener);
        holder.accountTypeBut.setEnabled(model.isValid());
        holder.accountTypeBut.setTag(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
