package com.example.chat.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chat.Client;
import com.example.chat.pojo.Msg;
import com.example.chat.adapter.MsgAdapter;
import com.example.chat.R;
import com.example.chat.service.ChatService;
import com.example.chat.pojo.Chat;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;


//    Chat chat = new Chat();

    private String toName =null;
    private String name =null;
    private List<String> stringList = new ArrayList<>();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
    private String time = null;

    private ChatService.ChatBinder chatBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            chatBinder = (ChatService.ChatBinder)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    private LocationReceiver locationReceiver = new LocationReceiver();
    //内部类，实现BroadcastReceiver
    private class LocationReceiver extends BroadcastReceiver {
        //必须要重载的方法，用来监听是否有广播发送
        @Override
        public void onReceive(final Context context, Intent intent) {
            String intentAction = intent.getAction();

            final List<String> list = intent.getStringArrayListExtra("msg");
            if (intentAction.equals("location.reportsucc") && list!=null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what=0x12;
                        Bundle data = new Bundle();
                        data.putStringArrayList("list", (ArrayList<String>) list);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                    Handler handler = new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what==0x12) {
                                Bundle data = msg.getData();
                                List<String> list = data.getStringArrayList("list");
                                for(String s:list)
                                {
                                    if(s != null){
                                        Msg msg1 = new Msg(s,Msg.TYPE_RECEIVED);
                                        msgList.add(msg1);


                                        Date date = new Date(System.currentTimeMillis());

                                        String currentDate = simpleDateFormat.format(date);

                                        Chat chat = new Chat();
                                        chat.setName(toName);
                                        chat.setMsg(s);
                                        chat.setState("receive");
                                        chat.setTime(currentDate);
                                        Log.d("chat",simpleDateFormat.toString());
                                        chat.save();
                                        adapter.notifyItemInserted(msgList.size()-1);
                                        msgRecyclerView.scrollToPosition(msgList.size()-1);
                                        inputText.setText("");
                                    }

                                }
                                Client.msg.clear();

                            }
                        }
                    };
                }).start();

            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent1 = getIntent();
        Bundle bundle = intent1.getBundleExtra("one");
        if(bundle != null){
            toName = bundle.getString("toName");
            name = bundle.getString("name");
            stringList = bundle.getStringArrayList("list");
            time = bundle.getString("time");
        }

        Intent intent = new Intent(ChatActivity.this,ChatService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);


        IntentFilter filter = new IntentFilter();
        filter.addAction("location.reportsucc");
        registerReceiver(locationReceiver, filter);


        initMsgs();
        inputText = (EditText)findViewById(R.id.input_text);
        send = (Button)findViewById(R.id.send);
        msgRecyclerView = (RecyclerView)findViewById(R.id.msg_recycler_view);
        final LinearLayoutManager layoutManage = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManage);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        if(stringList != null){
            for(String s:stringList)
            {
                if(s != null){
                    Msg msg1 = new Msg(s,Msg.TYPE_RECEIVED);
                    msgList.add(msg1);
                    Chat chat = new Chat();
                    chat.setName(toName);
                    chat.setMsg(s);
                    chat.setState("receive");
                    if(time != null){
                        chat.setTime(time);
                    }
                    chat.save();
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");
                }
            }
            Client.msg.clear();
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = inputText.getText().toString();
                if(!"".equals(content)){
                    Msg msg = new Msg(content,Msg.TYPE_SENT);
                    msgList.add(msg);
                    Date date = new Date(System.currentTimeMillis());

                    String currentDate = simpleDateFormat.format(date);
                    Chat chat = new Chat();
                    chat.setName(toName);
                    chat.setMsg(content);
                    chat.setState("send");
                    chat.setTime(currentDate);
                    chat.save();
                    chatBinder.sendMsg(toName,content);
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");
                }
            }
        });
    }

    private void initMsgs() {
        List<Chat> chats = DataSupport.findAll(Chat.class);
        for(Chat c :chats){
            if(toName.equals(c.getName())){
                if("receive".equals(c.getState())){
                    Msg msg1 = new Msg(c.getMsg(),Msg.TYPE_RECEIVED);
                    msgList.add(msg1);
                }
                if("send".equals(c.getState())){
                    Msg msg1 = new Msg(c.getMsg(),Msg.TYPE_SENT);
                    msgList.add(msg1);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(locationReceiver);

        msgList.clear();

        unbindService(connection);
        super.onDestroy();
    }
}
