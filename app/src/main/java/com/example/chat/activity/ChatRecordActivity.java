package com.example.chat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.chat.adapter.ChatRecordAdapter;
import com.example.chat.pojo.Chatlog;
import com.example.chat.R;
import com.example.chat.pojo.Chat;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ChatRecordActivity extends AppCompatActivity {

    private String userName = null;
    private String name = null;
    private String toName = null;
    private List<Chatlog> chatlogList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_record);

        Bundle bundle = getIntent().getBundleExtra("one");
        userName = bundle.getString("userName");
        name = bundle.getString("name");
        toName = bundle.getString("toName");

        TextView textView = (TextView) findViewById(R.id.name);
        textView.setText(name);

        initChatlogs();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(LayoutManager);
        ChatRecordAdapter adapter = new ChatRecordAdapter(chatlogList);
        recyclerView.setAdapter(adapter);
    }

    private void initChatlogs(){
        List<Chat> chats = DataSupport.where("name = ?",toName).find(Chat.class);
        for(Chat chat: chats){
            String itime = chat.getTime();
            String imsg = chat.getMsg();
            if("send".equals(chat.getState())) {
                chatlogList.add(new Chatlog(userName, itime, imsg));
            }
            else{
                chatlogList.add(new Chatlog(name,itime,imsg));
            }
        }

    }
}

