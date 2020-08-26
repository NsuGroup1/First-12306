package com.example.a12306f.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OpenHelper extends SQLiteOpenHelper {
    private final  static String CREATE_TABLE_SQL = "create table history_detail(_id integer primary key autoincrement,startC,endC)";

    private final  static String CREATE_TABLE_SQL1 = "create table person_detail(_id integer primary key autoincrement,addName,idCard,tel)";




    public OpenHelper(Context context){
        super(context,"history_list",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("LnDBOpenHelper","---onCreate called ----");
        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
        sqLiteDatabase.execSQL(CREATE_TABLE_SQL1);//这是再创建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.i("LnDBOpenHelper","----onUpgrade called----");
        sqLiteDatabase.execSQL("drop table if exists history_detail");
        sqLiteDatabase.execSQL("drop table if exists person_detail");
        onCreate(sqLiteDatabase);
    }
}