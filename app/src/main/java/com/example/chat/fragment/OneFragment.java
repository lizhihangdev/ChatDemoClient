package com.example.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chat.Client;
import com.example.chat.adapter.ItemsAdapter;
import com.example.chat.listener.OnRecyclerViewClickListener;
import com.example.chat.R;
import com.example.chat.activity.ChatActivity;
import com.example.chat.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chen on 2019/5/16.
 */

public class OneFragment extends Fragment {

    private ArrayList<String> data = new ArrayList<>();

    private RecyclerView recyclerView;

    private List<String> friendIdList;

    private List<String> friendNameList;

    private String hostname = null;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;

    public OneFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_one_fragment,container,false);


        final MainActivity activity = (MainActivity)getActivity();

        Bundle bundle = OneFragment.this.getArguments();
        hostname = bundle.getString("name");
        friendIdList = bundle.getStringArrayList("friendIdList");
        friendNameList = bundle.getStringArrayList("friendNameList");
        data = (ArrayList<String>) friendNameList;

        TextView nameText = (TextView)view.findViewById(R.id.name);
        nameText.setText(Client.getUserName());

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);



        ItemsAdapter adapter = new ItemsAdapter(data);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new OnRecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View view,int position) {
                Intent intent = new Intent(activity,ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("toName",friendIdList.get(position));
                bundle.putString("name",friendNameList.get(position));
                Date date = new Date(System.currentTimeMillis());

                String currentDate = simpleDateFormat.format(date);
                bundle.putString("time",currentDate);
                bundle.putStringArrayList("list", (ArrayList<String>) Client.msg);
                intent.putExtra("one",bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClickListener(View view) {

            }
        });


        return view;
    }


}
