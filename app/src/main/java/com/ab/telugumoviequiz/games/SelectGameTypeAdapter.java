package com.ab.telugumoviequiz.games;

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

public class SelectGameTypeAdapter extends RecyclerView.Adapter<SelectGameTypeAdapter.MyViewHolder> {

    private final List<GameTypeModel> data;
    private final View.OnClickListener listener;

    public SelectGameTypeAdapter(List<GameTypeModel> data, View.OnClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView celebrityNameTxt;
        Button gameTypeBut;

        MyViewHolder(View view) {
            super(view);
            CardView cardView = view.findViewById(R.id.gameTypeCardEntry);
            cardView.getLayoutParams().width = Utils.getScreenWidth(cardView.getContext())[0] / 2;
            cardView.getLayoutParams().height = Utils.getScreenWidth(cardView.getContext())[1] / 2;

            gameTypeBut = cardView.findViewById(R.id.gameTypeEntryBut);
            gameTypeBut.getLayoutParams().width = Utils.getScreenWidth(cardView.getContext())[0] / 4;
            gameTypeBut.getLayoutParams().height = Utils.getScreenWidth(cardView.getContext())[1] / 3;
            celebrityNameTxt = cardView.findViewById(R.id.gameTypeEntryName);
        }
    }

    @NonNull
    @Override
    public SelectGameTypeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_type_entry, parent, false);
        return new SelectGameTypeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SelectGameTypeAdapter.MyViewHolder holder, int position) {
        GameTypeModel model = data.get(position);
        System.out.println(model);
        holder.gameTypeBut.setText(model.getGameTypeName());
        holder.celebrityNameTxt.setText(model.getCelebrityName());
        holder.gameTypeBut.setOnClickListener(listener);
        holder.gameTypeBut.setTag(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
