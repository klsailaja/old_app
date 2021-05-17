package com.ab.telugumoviequiz.money;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.Utils;
import com.ab.telugumoviequiz.games.PayGameModel;

import java.util.List;

public class ViewAccountsAdapter extends RecyclerView.Adapter<ViewAccountsAdapter.MyViewHolder> {

    private final List<PayGameModel> data;
    int[] points;
    static int width;
    static int height;

    public ViewAccountsAdapter(List<PayGameModel> data, Context context) {
        this.data = data;
        points = Utils.getScreenWidth(context);
        width = (points[0] * 4)/4;
        height = (points[1] * 4)/4;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView accountTypeTxt;
        TextView accountBalance;


        MyViewHolder(View view) {
            super(view);
            CardView cardView = view.findViewById(R.id.gameTypeCardEntry);
            cardView.getLayoutParams().width = width / 3;
            //cardView.getLayoutParams().height = height / 4;

            accountTypeTxt = cardView.findViewById(R.id.gameTypeEntryTxt);
            accountTypeTxt.getLayoutParams().width = width / 6;
            //accountTypeTxt.getLayoutParams().height = height / 3;
            accountBalance = cardView.findViewById(R.id.gameTypeEntryName);
        }
    }

    @NonNull
    @Override
    public ViewAccountsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallet_list_entry_view, parent, false);
        return new ViewAccountsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewAccountsAdapter.MyViewHolder holder, int position) {
        PayGameModel model = data.get(position);
        holder.accountTypeTxt.setText(model.getAccountName());
        holder.accountBalance.setText(model.getAccountBalance());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
