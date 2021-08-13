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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.MyViewHolder> {

    private List<GameDetails> gameDetailsList;

    private final StringBuffer stringBuffer = new StringBuffer();

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
    private View.OnClickListener mClickListener;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView gameIdView, announceTimeView, dateView, countView, timeView, costView;
        Button joinButton;
        MyViewHolder(View view) {
            super(view);
            CardView cardView = view.findViewById(R.id.gameCardEntry);
            cardView.getLayoutParams().width =  Utils.getScreenWidth(cardView.getContext())[0]/ 2;

            gameIdView = view.findViewById(R.id.card_entry_gameId);
            announceTimeView = view.findViewById(R.id.card_entry_prizeMoneyId);
            dateView = view.findViewById(R.id.card_entry_date);
            countView = view.findViewById(R.id.card_entry_currentCount);
            timeView = view.findViewById(R.id.card_entry_time);
            costView = view.findViewById(R.id.card_entry_cost);
            joinButton = view.findViewById(R.id.card_entry_join);
        }
    }

    public GameAdapter() {
    }

    public void setGameDetailsList(List<GameDetails> gameDetailsList) {
        this.gameDetailsList = gameDetailsList;
    }

    public void setClickListener(View.OnClickListener listener) {
        this.mClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_entry, parent, false);
        Button loginButton = itemView.findViewById(R.id.card_entry_join);
        loginButton.setOnClickListener(mClickListener);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        GameDetails gd = gameDetailsList.get(position);
        Date date = new Date(gd.getStartTime());

        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append(gd.getTempGameId());
        holder.gameIdView.setText(stringBuffer.toString());

        String timePattern = "hh:mm aaa";
        simpleDateFormat.applyPattern(timePattern);
        String timeStr = simpleDateFormat.format(date);

        holder.announceTimeView.setText(timeStr);

        String datePattern = "MMM dd";
        simpleDateFormat.applyPattern(datePattern);
        String dateStr = simpleDateFormat.format(date);

        holder.dateView.setText(dateStr);
        holder.timeView.setText(timeStr);

        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append(gd.getCurrentCount());
        holder.countView.setText(stringBuffer.toString());

        stringBuffer.delete(0, stringBuffer.length());
        if (gd.getTicketRate() == 0) {
            stringBuffer.append("FREE");
        } else {
            stringBuffer.append(gd.getTicketRate());
        }
        holder.costView.setText(stringBuffer.toString());
        holder.joinButton.setTag(String.valueOf(position));
        if (gd.getGameType() == 2) {
            String joinTxt = "Join " + gd.getCelebrityName() + " Special";
            holder.joinButton.setText(joinTxt);
        }
    }
    @Override
    public int getItemCount() {
        return gameDetailsList.size();
    }
}
