package com.example.chat.pojo;

public class Chatlog {
    private String name;
    private String log;
    private String time;
    public Chatlog(String name, String time, String log){
        this.name = name;
        this.log = log;
        this.time = time;
    }

    public String getName(){
        return name;
    }

    public String getLog(){
        return log;
    }

    public String getTime(){
        return time;
    }
}
