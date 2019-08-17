package com.example.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat.Client;
import com.example.chat.adapter.ItemsAdapter;
import com.example.chat.listener.OnRecyclerViewClickListener;
import com.example.chat.R;
import com.example.chat.activity.ChatRecordActivity;
import com.example.chat.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2019/5/16.
 */

public class ThreeFragment extends Fragment {

    private ArrayList<String> data = new ArrayList<>();

    private RecyclerView recyclerView;

    private List<String> friendIdList;

    private List<String> friendNameList;

    private String userName = null;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_three_fragment,container,false);

        final MainActivity activity = (MainActivity)getActivity();

        Bundle bundle = ThreeFragment.this.getArguments();
        userName = Client.getUserName();
        friendIdList = bundle.getStringArrayList("friendIdList");
        friendNameList = bundle.getStringArrayList("friendNameList");
        data = (ArrayList<String>) friendNameList;

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);



        ItemsAdapter adapter = new ItemsAdapter(data);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new OnRecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View view,int position) {
                Intent intent = new Intent(activity,ChatRecordActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("toName",friendIdList.get(position));
                bundle.putString("name",friendNameList.get(position));
                bundle.putString("userName", userName);
                intent.putExtra("one",bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClickListener(View view) {

            }
        });

        return view;
    }

    public ThreeFragment(){

    }

}
