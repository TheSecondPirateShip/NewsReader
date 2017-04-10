package com.crews.newsreader.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/4/10.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper {
    /**
     * 建表
     */
    public static final String CREATE_DATA = "create table Data(" +
            "date text primary key," +
            "title text," +
            "thumbnail text," +
            "commentsUrl text," +
            "comments text" +
            "type text" +
            "url text)";

    private Context mContext;
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
        Toast.makeText(mContext,"数据库创建成功",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
