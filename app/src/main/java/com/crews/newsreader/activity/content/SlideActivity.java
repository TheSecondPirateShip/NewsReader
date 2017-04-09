package com.crews.newsreader.activity.content;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.crews.newsreader.R;
import com.crews.newsreader.adapters.recycler_slide;
import com.crews.newsreader.beans.Content.Body;
import com.crews.newsreader.beans.Content.Content;
import com.crews.newsreader.beans.Content.Slides;
import com.crews.newsreader.beans.Main.New;
import com.crews.newsreader.utils.HttpUtil;
import com.google.gson.Gson;

import java.util.List;

public class SlideActivity extends AppCompatActivity {
    /**
     * 问题：目前使用scrollview嵌套recyclerView
     * 解决：用header
     */
    private New mNew;
    private TextView header;
    private RecyclerView recyclerView;
    private String url;
    private String title;
    recycler_slide adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        init();
        bind();
        setRecyclerView();
        getFromHttp();
    }

    private void bind(){
        recyclerView = (RecyclerView)findViewById(R.id.slide_recycler);
        header = (TextView)findViewById(R.id.slide_header);
        header.setText(title);
    }

    private void setRecyclerView(){
         adapter= new recycler_slide(new recycler_slide.CallBack() {
            @Override
            public void onClick(Slides slides) {
                //点击图片
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
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
        mNew = (New)intent.getSerializableExtra("new");
        url = mNew.getLink().getUrl();
        title = mNew.getTitle();
    }
}
