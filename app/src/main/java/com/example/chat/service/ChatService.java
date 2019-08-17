package com.example.chat.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.chat.activity.ChatActivity;
import com.example.chat.Client;
import com.example.chat.R;
import com.example.chat.listener.ReceiveListener;

import java.util.ArrayList;
import java.util.List;

public class ChatService extends Service {


    private ReceiveListener receiveListener = new ReceiveListener() {
        @Override
        public void isReceive(String from) {//收到消息发送广播
            Intent intent = new Intent();
            intent.putStringArrayListExtra("msg", (ArrayList<String>) Client.msg);
            intent.putExtra("name",from);//from为发送者
            intent.setAction("location.reportsucc");
            sendBroadcast(intent);
        }

        @Override
        public void isLogin(String name,List<String> friendIdList,List<String> friendNameList) {//登陆成功发送广播
            Intent intent = new Intent();
            intent.setAction("success");
            //传递数据
            intent.putExtra("name",name);//账号
            intent.putStringArrayListExtra("friendIdList", (ArrayList<String>) friendIdList);//好友账号list
            intent.putStringArrayListExtra("friendNameList", (ArrayList<String>) friendNameList);//好友姓名list
            sendBroadcast(intent);
        }

        @Override
        public void notLogin() {//登陆失败发送广播
            Intent intent = new Intent();
            intent.setAction("error");
            sendBroadcast(intent);

        }

        @Override
        public void logined(String from) {//好友上线通知
            Intent intent1 = new Intent(ChatService.this,ChatActivity.class);
            PendingIntent pi = PendingIntent.getActivity(ChatService.this,0,intent1,0);

            Notification notification = new NotificationCompat.Builder(ChatService.this)
                    .setContentTitle("上线通知")
                    .setContentText(from+"上线了！")//好友名
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .build();
            startForeground(2,notification);
        }
    };

    public ChatService() {
    }

    public class ChatBinder extends Binder{

        private Client client = new Client(receiveListener);

        /**
         * 发送消息
         * @param chat 发送给chat
         * @param msg 消息内容
         */
        public void sendMsg(final String chat, final String msg) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if ("group".equals(chat.toLowerCase())) {
                        //群聊
                        client.sendMsg("{\"type\":\"MESSAGE\",\"chat\":\"GROUP\",\"from\":\""+Client.getThisName()+"\",\"to\":\"群号：xxxx\",\"message\":\"" + msg + "\"}");
                    } else {
                        //私聊
                        client.sendMsg("{\"type\":\"MESSAGE\",\"chat\":\"PRIVATE\",\"from\":\""+Client.getThisName()+"\",\"to\":\"" + chat + "\",\"message\":\"" + msg + "\"}");
                        //发送广播

                    }
                }
            }).start();

        }

        /**
         * 登陆
         * @param name 账号
         * @param password 密码
         */
        public void starClient(final String name, final String password) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.open(name,password);
                }
            }).start();

        }

        /**
         * 退出登陆
         */
        public void closeClient() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.close();
                }
            }).start();

        }

    }

    private ChatBinder mBinder = new ChatBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
