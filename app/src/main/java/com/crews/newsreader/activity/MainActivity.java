package com.crews.newsreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.crews.newsreader.R;
import com.crews.newsreader.adapters.recycler;
import com.crews.newsreader.beans.Data;
import com.crews.newsreader.beans.New;
import com.crews.newsreader.utils.HttpUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainTest";
    private RecyclerView recyclerView;
    private List<New> newList;
    private recycler adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind();
        getFromHttp();
        setRecyclerView();
    }

    private void bind(){
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        newList = new ArrayList<>();
    }

    /**
     * 设置recyclerView
     */
    private void setRecyclerView(){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new recycler(new recycler.CallBack() {
            @Override
            public void onClick(New item) {
                if(item != null) {
                    Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                    //传递这个新闻的类
                    intent.putExtra("new", item);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * 从网络中加载
     */
    private void getFromHttp(){
        String url = "http://api.irecommend.ifeng.com/irecommendList.php?userId=866048024885909&count=6&gv=5.2.6&av=5.2.6&uid=866048024885909&deviceid=866048024885909&proid=ifengnews&os=android_23&df=androidphone&vt=5&screen=720x1280&publishid=2024&nw=wifi&city=";
        HttpUtil.sendHttpRequest(url, new HttpUtil.CallBack() {
            @Override
            public void onFinish(String response) {
                Data data = gsonData(response);
                //加载到集合
                newList.addAll(data.getItem());
                showLog();
                //刷新recycler
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refresh(newList);
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
        for (New n : newList) {
            Log.d(TAG,n.getTitle());
        }

    }
}
