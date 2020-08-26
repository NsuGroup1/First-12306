package com.example.a12306f.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;



public class History{
    private ListView listView_history;
    private OpenHelper OpenHelper;
    private static final String TABLENAME="history_detail";
    public History(Context context){
        OpenHelper = new OpenHelper(context);
    }

    public History() {

    }


    public void insert(String startC,String endC){
//        Log.d("insert",query().size()+"");
        SQLiteDatabase sqLiteDatabase = OpenHelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("startC",startC);
        contentValues.put("endC",endC);

        sqLiteDatabase.insert(TABLENAME,null,contentValues);
        sqLiteDatabase.close();
    }

    public ArrayList query(){
        SQLiteDatabase sqLiteDatabase = OpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLENAME,new String[]{"startC","endC"},null,null,null,null,null);
        ArrayList arrayList = new ArrayList();
        if(cursor.getCount()==0){
            return arrayList;
        }
        else{
            while (cursor.moveToNext()){
                HashMap map = new HashMap();
                map.put("startC",cursor.getString(cursor.getColumnIndex("startC")));
                map.put("endC",cursor.getString(cursor.getColumnIndex("endC")));
                arrayList.add(map);
            }
        }
        cursor.close();
        OpenHelper.close();
        if(arrayList.size()>2){
            ArrayList arrayList1=new ArrayList();
            arrayList1.add(arrayList.get(arrayList.size()-1));
            arrayList1.add(arrayList.get(arrayList.size()-2));
            return arrayList1;
        }else {
            return  arrayList;
        }
    }
}
