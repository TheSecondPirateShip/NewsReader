package com.crews.newsreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.crews.newsreader.R;
import com.crews.newsreader.activity.content.DocActivity;
import com.crews.newsreader.activity.content.PhvideoActivity;
import com.crews.newsreader.activity.content.SlideActivity;
import com.crews.newsreader.adapters.recycler;
import com.crews.newsreader.beans.Main.Data;
import com.crews.newsreader.beans.Main.Item;
import com.crews.newsreader.utils.HttpUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainTest";
    private RecyclerView recyclerView;
    private List<Item> itemList;
    private recycler adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView view = (ImageView)findViewById(R.id.zctt) ;
        view.setFocusable(true);//启动app时把焦点放在其他控件（不放在editext上）上防止弹出虚拟键盘
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        bind();
        getFromHttp();
        setRecyclerView();
    }

    private void bind(){
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
    private void getFromHttp(){
        String url = "http://api.irecommend.ifeng.com/irecommendList.php?userId=866048024885909&count=6&gv=5.2.6&av=5.2.6&uid=866048024885909&deviceid=866048024885909&proid=ifengnews&os=android_23&df=androidphone&vt=5&screen=720x1280&publishid=2024&nw=wifi&city=";
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
                        adapter.refresh(itemList);
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

    private void toActivity(Class c,Item item){
        Intent intent = new Intent(MainActivity.this, c);
        //传递这个新闻的类
        intent.putExtra("new", item);
        startActivity(intent);
    }
}
