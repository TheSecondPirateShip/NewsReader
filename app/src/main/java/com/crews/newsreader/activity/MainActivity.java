package com.crews.newsreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.crews.newsreader.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind();
        getFromHttp();
    }

    private void bind(){
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        newList = new ArrayList<>();
    }

    private void getFromHttp(){
        String url = "http://api.irecommend.ifeng.com/irecommendList.php?userId=866048024885909&count=6&gv=5.2.6&av=5.2.6&uid=866048024885909&deviceid=866048024885909&proid=ifengnews&os=android_23&df=androidphone&vt=5&screen=720x1280&publishid=2024&nw=wifi&city=";
        HttpUtil.sendHttpRequest(url, new HttpUtil.CallBack() {
            @Override
            public void onFinish(String response) {
                Data data = gsonData(response);
                newList.addAll(data.getItem());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        show();
                    }
                });
            }
        });
    }

    private Data gsonData(String response){
        Gson gson = new Gson();
        return gson.fromJson(response, Data.class);
    }

    private void show(){
        for (New n : newList) {
            Log.d(TAG,n.getTitle());
        }
    }
}
