package com.example.chat;

import android.util.Log;

import com.example.chat.listener.ReceiveListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 客户端
 */
@SuppressWarnings("Duplicates")
public class Client {
    private static String serverInetAddress = "39.96.18.40";
    private static int serverPort = 5678;
    private static Socket client = null;
    private static OutputStream os = null;
    private static InputStream is = null;
    private static String thisName;//账号
    private static String userName;//用户名
    private static boolean alive = true;
    private ReceiveListener listener;//接收监听器

    public static List<String> msg = new ArrayList<>();//消息list

    public Client(ReceiveListener listener){
        this.listener = listener;
    }

    /**
     * 客户端连接服务器
     * @param name 用户账号
     * @param password 密码
     */
    @SuppressWarnings("unused")
    public void open(String name,String password) {

        try {
            thisName = name;//账号
            InetAddress inetAddress = InetAddress.getLocalHost();
            //建立连接
            client = new Socket(serverInetAddress, serverPort);
            //数据流发送数据
            os = client.getOutputStream();
            sendMsg("{\"type\":\"OPEN\",\"clientName\":\"" + name + "\",\"password\":\""+password+"\"}");
            //数据流接收数据
            is = client.getInputStream();
            byte[] b = new byte[1024];
            int length = 0;
            while (alive) {
                //接收从服务器发送回来的消息
                length = is.read(b);
                if (length != -1) {
                    onMsg(new String(b, 0, length));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关流
                os.close();
                client.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 关闭客户端
     */
    public void close() {
        sendMsg("{\"type\":\"CLOSE\"}");
        alive = false;
    }

    /**
     * 发送消息
     * @param msg json消息格式的String
     */
    public void sendMsg(String msg) {
        try {
            //调用发送
            os.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收到消息的回调
     * @param message 消息内容
     */
    private  void onMsg(final String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //JSON字符串转 HashMap
                HashMap hashMap = null;
                try {
                    hashMap = new ObjectMapper().readValue(message, HashMap.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String type = (String) hashMap.get("type");

                if ("OPEN".equals(type)) {//登陆类型
                    String clientName = (String) hashMap.get("clientName");//账号
                    String result = (String) hashMap.get("result");//结果

                    userName = (String) hashMap.get("userName");//用户名


                    List<String> friendIdList = new ArrayList<>();//好友账号list
                    List<String> friendNameList = new ArrayList<>();//好友姓名list

                    int userNum = Integer.parseInt(String.valueOf(hashMap.get("userNum")));//好友个数
                    for(int i=0;i<userNum;i++){
                        friendIdList.add((String) hashMap.get("userId"+String.valueOf(i)));
                        friendNameList.add((String) hashMap.get(friendIdList.get(i)));
                    }

                    if("success".equals(result)){
                        Log.d("Client","登陆成功");
                        listener.isLogin(clientName,friendIdList,friendNameList);//登陆成功监听
                    }
                    if("error".equals(result)){
                        Log.d("Client","登陆失败");
                        listener.notLogin();//登陆失败监听
                    }
                }

                if("MESSAGE".equals(type)){//消息类型
                    if (hashMap.get("message") != null){
                        msg.add((String) hashMap.get("message"));
                    }


                    String chat = (String) hashMap.get("chat");

                    String from = (String) hashMap.get("from");

                    String to = (String) hashMap.get("to");

                    //群聊
                    if ("GROUP".equals(chat)) {
                        //后台打印
                        Log.d("Client",thisName + "收到（" + to + "）群聊消息：" + msg.get(msg.size()-1));

                    }
                    //私聊
                    if ("PRIVATE".equals(chat)) {
                        //后台打印
                        Log.d("Client",thisName + "收到（" + from + "）私聊消息：" + msg.get(msg.size()-1));
                        listener.isReceive(from);
                    }
                    //好友上线
                    if ("LONGINED".equals(chat)) {
                        Log.d("Client",thisName + "收到（" + from + "）的上线消息");
                        listener.logined(from);//好友上线监听
                    }
                }

            }
        }).start();

    }

    /**
     * 获取thisName,thisName为账号
     */
    public static String getThisName() {
        return thisName;
    }

    /**
     * 获取msg
     * @return
     */
    public static List<String> getMsg(){
        return msg;
    }

    /**
     * 获取用户名
     * @return
     */
    public static String getUserName(){
        return userName;
    }


}