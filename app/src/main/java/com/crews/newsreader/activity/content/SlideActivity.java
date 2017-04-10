package com.crews.newsreader.activity.content;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.crews.newsreader.R;
import com.crews.newsreader.adapters.recycler_slide;
import com.crews.newsreader.beans.Content.Content;
import com.crews.newsreader.beans.Content.Slides;
import com.crews.newsreader.beans.Main.Item;
import com.crews.newsreader.utils.HttpUtil;
import com.google.gson.Gson;

import java.util.List;

public class SlideActivity extends AppCompatActivity {
    /**
     * 问题：目前使用scrollview嵌套recyclerView
     * 解决：用header
     */
    private Item mItem;
    private RecyclerView recyclerView;
    private String url;
    private String title;
    private recycler_slide adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        bind();
        init();
        setRecyclerView();
        getFromHttp();
    }

    private void bind(){
        recyclerView = (RecyclerView)findViewById(R.id.slide_recycler);
    }

    private void setRecyclerView(){
        adapter= new recycler_slide(new recycler_slide.CallBack() {
            @Override
            public void onClick(Slides slides) {
                //点击图片
            }
        },title);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        /*//横向滚动
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);*/
    }

    private void getFromHttp(){
        HttpUtil.sendHttpRequest(url, new HttpUtil.CallBack() {
            @Override
            public void onFinish(String response) {
                final List<Slides> list = gsonContent(response).getBody().getSlides();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refresh(list);
                    }
                });
            }
        });
    }

    private Content gsonContent(String response) {
        Gson gson = new Gson();
        return gson.fromJson(response, Content.class);
    }

    private void init(){
        Intent intent = getIntent();
        mItem = (Item)intent.getSerializableExtra("new");
        url = mItem.getLink().getUrl();
        title = mItem.getTitle();
    }
}
