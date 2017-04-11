package com.crews.newsreader.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crews.newsreader.R;
import com.crews.newsreader.activity.content.DocActivity;
import com.crews.newsreader.activity.content.PhvideoActivity;
import com.crews.newsreader.activity.content.SlideActivity;
import com.crews.newsreader.adapters.recycler;
import com.crews.newsreader.beans.Main.Data;
import com.crews.newsreader.beans.Main.Item;
import com.crews.newsreader.utils.HttpUtil;
import com.crews.newsreader.utils.MyDataBaseHelper;
import com.crews.newsreader.utils.SQUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainTest";
    private RecyclerView recyclerView;
    private EditText editText;
    private List<Item> itemList;
    private recycler adapter;
    private MyDataBaseHelper dbHelper;
    private LinearLayoutManager mLinearLayoutManager;
    private int lastVisibleItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLinearLayoutManager = new GridLayoutManager(this, 1);

        ImageView view = (ImageView)findViewById(R.id.zctt) ;
        view.setFocusable(true);//启动app时把焦点放在其他控件（不放在editext上）上防止弹出虚拟键盘
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        bind();
        getFromHttp(1);
        setRecyclerView();
        createSQ();

        setFootView();


    }

    private void setFootView() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 ==adapter.getItemCount()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getFromHttp(2);
                            Toast.makeText(MainActivity.this,"成功获取新数据",Toast.LENGTH_SHORT).show();
                        }
                    },1000);
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView,dx, dy);
                lastVisibleItem =mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void createSQ(){
        dbHelper = new MyDataBaseHelper(this,"Data.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        SQUtils utils = new SQUtils();
        //插入方法
        Log.d("666","数据库开始");
        utils.insert(db,values,itemList);
        String obj = editText.getText().toString();
        utils.query(db,values,obj);
    }


    private void bind(){
        editText = (EditText) findViewById(R.id.edit_query);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();
    }

    private void setRecyclerView(){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new recycler(new recycler.CallBack() {
            @Override
            public void onClick(Item item) {
                if(item.getType().equals("doc"))
                {
                    toActivity(DocActivity.class,item);
                }
                else if(item.getType().equals("slide"))
                {
                    toActivity(SlideActivity.class,item);
                }
                else if(item.getType().equals("phvideo")){
                    toActivity(PhvideoActivity.class,item);
                }
                /**
                 * 还有一个web类型的，这个是广告，需要在recycler中去掉
                 */
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * 从网络中加载
     */
    private void getFromHttp(final int mode){
        String url = "http://suo.im/1kHreH";
        HttpUtil.sendHttpRequest(url, new HttpUtil.CallBack() {
            @Override
            public void onFinish(String response) {
                Data data = gsonData(response);
                //加载到集合
                itemList.addAll(data.getItem());
                showLog();
                //刷新recycler
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mode ==1) {
                            relist();
                            adapter.refresh(itemList);
                        }
                        if(mode == 2){
                            relist();
                            adapter.addMoreItem(itemList);
                        }
                    }
                });
            }
        });
    }

    /**
     * 序列化
     * @param response String
     * @return Data
     */
    private Data gsonData(String response){
        Gson gson = new Gson();
        return gson.fromJson(response, Data.class);
    }

    /**
     * log测试
     */
    private void showLog(){
        for (Item n : itemList) {
            Log.d(TAG,n.getTitle());
        }

    }

    /**
     * 跳转内容界面
     * @param c 当前activity
     * @param item 新闻的item类
     */
    private void toActivity(Class c,Item item){
        Intent intent = new Intent(MainActivity.this, c);
        //传递这个新闻的类
        intent.putExtra("new", item);
        startActivity(intent);
    }

    private void relist(){
        List<Item> deletelist = new ArrayList<>();
        for (int i=0;i<itemList.size();i++) {
            if (itemList.get(i).getType().equals("web")) {
                deletelist.add(itemList.get(i));
            }
            if(itemList.get(i).getSource() == null){
                itemList.get(i).setSource("未知");
            }
            if(itemList.get(i).getUpdateTime() == null){
                itemList.get(i).setUpdateTime("未知");
            }
        }
        itemList.removeAll(deletelist);
    }
}
