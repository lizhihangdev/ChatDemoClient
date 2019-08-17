package com.example.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chat.R;
import com.example.chat.pojo.Chatlog;

import java.util.List;

public class ChatRecordAdapter extends RecyclerView.Adapter<ChatRecordAdapter.ViewHolder> {
    private List<Chatlog> mChatlogList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView chatName;
        TextView chatLog;
        TextView chatTime;

        public ViewHolder( View view) {
            super(view);
            chatName = (TextView)view.findViewById(R.id.name);
            chatTime = (TextView) view.findViewById(R.id.time);
            chatLog = (TextView)view.findViewById(R.id.msg);
        }
    }

    public ChatRecordAdapter(List<Chatlog> chatlogList){
        mChatlogList = chatlogList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_record_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }


    @Override
    public void onBindViewHolder(@NonNull ChatRecordAdapter.ViewHolder viewHolder, int position) {
        Chatlog chatlog = mChatlogList.get(position);
        viewHolder.chatName.setText(chatlog.getName());
        viewHolder.chatTime.setText((chatlog.getTime()));
        viewHolder.chatLog.setText(chatlog.getLog());
    }

    @Override
    public int getItemCount() {
        return mChatlogList.size();
    }
}
