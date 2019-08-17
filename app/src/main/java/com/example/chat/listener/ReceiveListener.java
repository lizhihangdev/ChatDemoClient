package com.example.chat.listener;

import java.util.List;

public interface ReceiveListener {
    void isReceive(String from);//接收监听
    void isLogin(String name, List<String> friendIdList, List<String> friendNameList);//登陆成功监听
    void notLogin();//登陆失败监听
    void logined(String from);//好友上线监听
}
