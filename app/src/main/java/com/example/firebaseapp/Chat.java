package com.example.firebaseapp;

public class Chat {

    private String sender;
    private String receiver;
    private String msg;
    private String uptime;
    private boolean isseen;

    public Chat() {
    }
    public Chat(String sender, String receiver, String msg,String uptime,boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.uptime = uptime;
        this.isseen = isseen;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
