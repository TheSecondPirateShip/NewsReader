package com.crews.newsreader.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.crews.newsreader.beans.Content.Content;
import com.crews.newsreader.beans.Main.Item;

import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */

public class SQUtils {
    private final String TAG = "SQUtils";
    MyDataBaseHelper dataBaseHelper = null;
    Context mContext = null;
    SQLiteDatabase db = null;



    public SQUtils(Context context){
        mContext = context;
        dataBaseHelper = new MyDataBaseHelper(context,"Data.db",null,1);
        db = dataBaseHelper.getWritableDatabase();
        Log.d(TAG,"创建数据库成功");
    }


    /**
     * 数据库的插入方法
     * @param itemList
     */
    public void insert(List<Item> itemList){
        if(db != null) {
            ContentValues values = new ContentValues();
            for (int i = 0; i < itemList.size(); i++) {
                values.put("date", itemList.get(i).getUpdateTime());
                values.put("commentsUrl", itemList.get(i).getCommentsUrl());
                values.put("thumbnail", itemList.get(i).getThumbnail());
                values.put("title", itemList.get(i).getTitle());
                values.put("type", itemList.get(i).getLink().getType());
                values.put("url", itemList.get(i).getLink().getUrl());
                values.put("comments", itemList.get(i).getComments());

                db.insert("Data", null, values);
                values.clear();
            }
            Log.d(TAG, "数据库插入没有问题");
        }
        else Log.d(TAG, "数据库错误：db == null");
    }

    /**
     * 数据库的删除方法（全部删除）
     */
    public void deleteAll(){
        db.delete("Data",null,null);
        Log.d(TAG, "数据库中数据全部删除");
    }

    /**
     * 更新方法
     */
    public void update(){
        //此方法暂时无用
    }
    public String query(String obj){
        Cursor cursor = db.query("Data",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Log.d(TAG,"开始查询了");
                String date = cursor.getString(cursor.getColumnIndex("date"));
                if(date.equals(obj)){

                    //返回日期，从SQ中取出当天的新闻;
                    return date;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return null;
    }



    class MyDataBaseHelper extends SQLiteOpenHelper {
        /**
         * 建表
         */
        public static final String CREATE_DATA = "create table Data(" +
                "id integer primary key autoincrement," +
                "date text," +
                "title text," +
                "thumbnail text," +
                "commentsUrl text," +
                "comments text," +
                "type text," +
                "url text)";

        Context mContext;

        public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            mContext = context;
        }

        /**
         * 数据库开始就调用此方法
         * @param sqLiteDatabase
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DATA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
