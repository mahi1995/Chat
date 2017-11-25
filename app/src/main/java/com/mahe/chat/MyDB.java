package com.mahe.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
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
            MyDBContract.TBL_GROUP.CLM_CHANNEL+"  TEXT  NOT NULL UNIQUE)";

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
            Log.i("ex:=",e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_CHAT);
        db.execSQL(DROP_GROUP);

        onCreate(db);
    }
    public long insertChat(Message m){
        SQLiteDatabase db=null;
        try {
            db= this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MyDBContract.TBL_CHAT.CLM_FROM, m.getFrom());
            values.put(MyDBContract.TBL_CHAT.CLM_MESSAGE, m.getMessage());
            values.put(MyDBContract.TBL_CHAT.CLM_ISMe, m.getIsMe());
            values.put(MyDBContract.TBL_CHAT.CLM_TIME, m.getTime());
            long result = db.insertOrThrow(MyDBContract.TBL_CHAT.TBL_CHAT, null, values);
            return result;
        }catch (Exception ex){

            Toast.makeText(ctx,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
        finally {
            if(db!=null)
                db.close();
        }
        return 0;
    }

    public long addChannel(String channel){
        SQLiteDatabase db=null;
        try {
            db =this.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(MyDBContract.TBL_GROUP.CLM_CHANNEL,channel);
            long result= db.insertOrThrow(MyDBContract.TBL_GROUP.TBL_GROUP,null,values);
            return result;
        }catch (Exception ex){
            Toast.makeText(ctx,ex.getMessage(),Toast.LENGTH_LONG).show();
            Log.i("ex:=",ex.getMessage());
        }
        finally {
            if(db!=null)
                db.close();
        }
        return 0;
    }

    public void deleteChannelRecords(){
        SQLiteDatabase db=null;
        try {
            db =this.getWritableDatabase();
            db.execSQL("delete from "+ MyDBContract.TBL_GROUP.TBL_GROUP);
        }catch (Exception ex){
            Toast.makeText(ctx,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
        finally {
            if(db!=null)
                db.close();
        }
    }


    public void deleteChatRecords(){
        SQLiteDatabase db=null;
        try {
            db =this.getWritableDatabase();
            db.execSQL("delete from "+ MyDBContract.TBL_CHAT.TBL_CHAT);
        }catch (Exception ex){
            Toast.makeText(ctx,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
        finally {
            if(db!=null)
                db.close();
        }
    }

    public ArrayList<String> getAllChannels(){
        SQLiteDatabase db=null;
        ArrayList<String> l=new ArrayList<>();
        try {
            db =this.getWritableDatabase();
            Cursor c=db.query(MyDBContract.TBL_GROUP.TBL_GROUP,new String[]{MyDBContract.TBL_GROUP.CLM_CHANNEL},null,null,null,null, MyDBContract.TBL_GROUP._ID);
            if(c.moveToFirst()){
             do {
                 l.add(c.getString(0));
             }while (c.moveToNext());
            }
        }catch (Exception ex){
            Toast.makeText(ctx,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
        finally {
            if(db!=null)
                db.close();
        }
        return l;
    }


    public ArrayList<String> getDistinctChats(){
        SQLiteDatabase db=null;

        ArrayList<String> l=new ArrayList<>();
        try {
            db =this.getWritableDatabase();
            Cursor c=db.query(true,MyDBContract.TBL_CHAT.TBL_CHAT,new String[]{MyDBContract.TBL_CHAT.CLM_FROM},null,null,null,null, MyDBContract.TBL_CHAT.CLM_TIME+" DESC",null);
            if(c.moveToFirst()){
                do {
                    l.add(c.getString(0));
                }while (c.moveToNext());
            }
        }catch (Exception ex){
            Toast.makeText(ctx,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
        finally {
            if(db!=null)
                db.close();
        }
        return l;
    }

    public ArrayList<Message> getMessage(String fromWho){
        SQLiteDatabase db=null;

        ArrayList<Message> l=new ArrayList<>();
        try {
            db =this.getWritableDatabase();
            Cursor c=db.query(MyDBContract.TBL_CHAT.TBL_CHAT,new String[]{MyDBContract.TBL_CHAT.CLM_MESSAGE, MyDBContract.TBL_CHAT.CLM_ISMe, MyDBContract.TBL_CHAT.CLM_TIME}, MyDBContract.TBL_CHAT.CLM_FROM+"=?",new String[]{fromWho},null,null, MyDBContract.TBL_CHAT.CLM_TIME);
            if(c.moveToFirst()){
                do {
                    Message m=new Message(fromWho,c.getString(0),c.getString(2),Integer.parseInt(c.getString(1)),false);
                    l.add(m);
                }while (c.moveToNext());
            }
        }catch (Exception ex){
            Toast.makeText(ctx,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
        finally {
            if(db!=null)
                db.close();
        }
        return l;
    }

}
