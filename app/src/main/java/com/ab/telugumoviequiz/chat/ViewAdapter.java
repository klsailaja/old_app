package com.ab.telugumoviequiz.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.telugumoviequiz.R;
import com.ab.telugumoviequiz.common.UserDetails;

import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.MyViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private static final int MSG_TYPE_HEADING = 2;
    private final List<Chat> msgs;

    public ViewAdapter(List<Chat> msgs) {
        this.msgs = msgs;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userNameView, msgView, timeView;

        MyViewHolder(View view) {
            super(view);
            userNameView = view.findViewById(R.id.chat_name_txt);
            msgView = view.findViewById(R.id.chat_msg_txt);
            timeView = view.findViewById(R.id.chat_time_txt);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_time_heading, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        Chat message = msgs.get(position);
        if (viewType == MSG_TYPE_RIGHT) {
            holder.msgView.setText(message.getMessage());
            holder.timeView.setText(message.getStrTime());
        } else if (viewType == MSG_TYPE_LEFT) {
            holder.msgView.setText(message.getMessage());
            holder.userNameView.setText(message.getSenderName());
            holder.timeView.setText(message.getStrTime());
        } else {
            holder.timeView.setText(message.getStrTime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        Chat chatMsg = msgs.get(position);
        if (chatMsg.getSenderUserId() == -1) {
            return MSG_TYPE_HEADING;
        }
        if (UserDetails.getInstance().getUserProfile().getId() == chatMsg.getSenderUserId()) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }
}
