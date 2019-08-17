package com.example.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chat.R;
import com.example.chat.listener.OnRecyclerViewClickListener;

import java.util.ArrayList;


public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private ArrayList<String> list;

    public ItemsAdapter(ArrayList<String> mlist) {
        list = mlist;
    }

    private OnRecyclerViewClickListener listener;
    public void setItemClickListener(OnRecyclerViewClickListener itemClickListener) {
        listener = itemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);

        final ViewHolder holder = new ViewHolder(view);
        holder.itemsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    int position =holder.getAdapterPosition();
                    listener.onItemClickListener(view,position);
                }

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String s = list.get(i);
        viewHolder.items.setText(s);
        viewHolder.img.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        View itemsView;
        ImageView img;
        TextView items;

        public ViewHolder(@NonNull View view) {
            super(view);
            itemsView = view;
            img = (ImageView)view.findViewById(R.id.img);
            items = (TextView)view.findViewById(R.id.item);
        }
    }


}
