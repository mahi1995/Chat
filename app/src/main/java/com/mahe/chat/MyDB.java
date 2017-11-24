package com.mahe.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by hp on 22-10-2017.
 */

public class MyDB extends SQLiteOpenHelper {

    public  static  final  int DB_VERSION=1;
    public static final String DB_NAME="MYCHAT.db";
    public  static  final  String CREATE_CHAT="CREATE TABLE "+ MyDBContract.TBL_CHAT.TBL_CHAT+" ("+
            MyDBContract.TBL_CHAT._ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
            MyDBContract.TBL_CHAT.CLM_FROM+"  TEXT ,"+
            MyDBContract.TBL_CHAT.CLM_MESSAGE+"  TEXT ,"+
            MyDBContract.TBL_CHAT.CLM_ISMe+" TEXT," +
            MyDBContract.TBL_CHAT.CLM_TIME+" TEXT)";
    public  static final  String DROP_CHAT="DROP TABLE IF EXISTS "+ MyDBContract.TBL_CHAT.TBL_CHAT;

    public  static final  String CREATE_GROUP="CREATE TABLE "+ MyDBContract.TBL_GROUP.TBL_GROUP+" ("+
            MyDBContract.TBL_GROUP._ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
            MyDBContract.TBL_GROUP.CLM_CHANNEL+"  TEXT )";

    public  static final  String DROP_GROUP="DROP TABLE IF EXISTS "+ MyDBContract.TBL_GROUP.TBL_GROUP;


    Context ctx;
    public MyDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        ctx=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_CHAT);
            db.execSQL(CREATE_GROUP);
        }catch (Exception e){
            Toast.makeText(ctx,e.toString(),Toast.LENGTH_LONG).show();
        }
        Toast.makeText(ctx,"TABLEs created",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_CHAT);
        db.execSQL(DROP_GROUP);

        onCreate(db);
    }
    public long insertChat(String from,String message,String time, String isME){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(MyDBContract.TBL_CHAT.CLM_FROM,from);
        values.put(MyDBContract.TBL_CHAT.CLM_MESSAGE,message);
        values.put(MyDBContract.TBL_CHAT.CLM_ISMe,isME);
        values.put(MyDBContract.TBL_CHAT.CLM_TIME,time);
      long result= db.insert(MyDBContract.TBL_CHAT.TBL_CHAT,null,values);
        db.close();
        return result;
    }

}
