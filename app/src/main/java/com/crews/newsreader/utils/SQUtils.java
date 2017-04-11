package com.crews.newsreader.utils;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.crews.newsreader.beans.Main.Item;

import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */

public class SQUtils {
    /**
     * 数据库的插入方法
     * @param db
     * @param values
     * @param itemList
     */
    public void insert(SQLiteDatabase db, ContentValues values, List<Item> itemList){
        for(int i = 0;i<itemList.size();i++){
            values.put("date",itemList.get(i).getUpdateTime());
            values.put("title",itemList.get(i).getTitle());
            values.put("thumbnail",itemList.get(i).getThumbnail());
            values.put("conmentsUrl",itemList.get(i).getCommentsUrl());
            values.put("conments",itemList.get(i).getComments());
            values.put("type",itemList.get(i).getLink().getType());
            values.put("url",itemList.get(i).getLink().getUrl());
            db.insert("Data",null,values);
            values.clear();
        }
        Log.d("123456","数据库插入没有问题");
    }

    /**
     * 数据库的删除方法（全部删除）
     * @param db
     * @param values
     */
    public void delete(SQLiteDatabase db,ContentValues values){
        db.delete("Data",null,null);
    }

    /**
     * 更新方法
     */
    public void update(){
        //此方法暂时无用
    }
    public void query(SQLiteDatabase db,ContentValues values,String obj){
        Cursor cursor = db.query("Data",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String date = cursor.getString(cursor.getColumnIndex("date"));
                if(date == obj){
                    //执行操作
                    break;
                }
            }while (cursor.moveToNext());
        }
    }
}
