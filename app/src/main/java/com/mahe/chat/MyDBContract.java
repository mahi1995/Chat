package com.mahe.chat;

import android.provider.BaseColumns;

/**
 * Created by hp on 22-10-2017.
 */

public class MyDBContract {

    private MyDBContract(){}

    public  static class TBL_CHAT implements BaseColumns{
        public  static final  String TBL_CHAT="chat";
        public  static final  String CLM_ISMe="isme";
        public  static final  String CLM_FROM="fromwho";//Chaneel as well
        public  static final  String CLM_MESSAGE="message";
        public  static final  String CLM_TIME="timestamp";
    }
    public static class TBL_GROUP implements  BaseColumns{
        public  static final  String TBL_GROUP="groups";
        public  static final  String CLM_CHANNEL ="channel";
    }

}
