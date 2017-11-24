package com.mahe.chat;

/**
 * Created by hp on 24-11-2017.
 */

public class Message {
   private String from,message,time;
    int isMe;
    Message(String f,String m,String t,int i){
        time=t;
        from=f;
        message=m;
        isMe=i;
    }

    public int getIsMe() {
        return isMe;
    }

    public void setIsMe(int isMe) {
        this.isMe = isMe;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
