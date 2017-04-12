package com.crews.newsreader.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.crews.newsreader.R;
import com.crews.newsreader.activity.content.DocActivity;
import com.crews.newsreader.activity.content.PhvideoActivity;
import com.crews.newsreader.activity.content.SlideActivity;
import com.crews.newsreader.adapters.recycler;
import com.crews.newsreader.beans.Main.Data;
import com.crews.newsreader.beans.Main.Item;
import com.crews.newsreader.beans.Main.Link;
import com.crews.newsreader.utils.HttpUtil;
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
    //数据库
    private SQUtils sqUtil;
    private LinearLayoutManager mLinearLayoutManager;
    private int lastVisibleItem;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //数据库查询的结果日期
    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView view = (ImageView)findViewById(R.id.zctt) ;
        view.setFocusable(true);//启动app时把焦点放在其他控件（不放在editext上）上防止弹出虚拟键盘
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        bind();
        setWidget();
        getFromSQL();
        setRecyclerView();

        setSwipeRefresh();
        setFootView();


    }

    private void setSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFromHttp(3);
            }
        });

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

    private void insertSQL(){
        sqUtil.insert(itemList);
    }


    private void bind(){
        editText = (EditText) findViewById(R.id.edit_query);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mLinearLayoutManager = new GridLayoutManager(this, 1);
        itemList = new ArrayList<>();
        sqUtil = new SQUtils(this);
    }

    /**
     * 启动时先从数据库中得到新闻
     */
    private void getFromSQL(){
            Cursor cursor = sqUtil.getCursor();
            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setUpdateTime(cursor.getString(cursor.getColumnIndex("data")));
                    Link link = new Link();
                    link.setType(cursor.getString(cursor.getColumnIndex("type")));
                    link.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                    item.setLink(link);
                    item.setCommentsUrl(cursor.getString(cursor.getColumnIndex("commentsUrl")));
                    item.setComments(cursor.getString(cursor.getColumnIndex("comments")));
                    item.setThumbnail(cursor.getString(cursor.getColumnIndex("thumbnail")));
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    itemList.add(item);
                } while (cursor.moveToNext());
            }
        else getFromHttp(1);
        cursor.close();
    }

    private void setWidget(){
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(MainActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    String obj = editText.getText().toString();
                    date = sqUtil.query(obj);
                    if(date != null){
                        Toast.makeText(view.getContext(),"查询成功", Toast.LENGTH_LONG).show();}
                    else {
                        Toast.makeText(view.getContext(),"无结果",Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });
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
        String url = "http://api.irecommend.ifeng.com/irecommendList.php?userId=866048024885909&count=6&gv=5.2.6&av=5.2.6&uid=866048024885909&deviceid=866048024885909&proid=ifengnews&os=android_23&df=androidphone&vt=5&screen=720x1280&publishid=2024&nw=wifi&city=";
        HttpUtil.sendHttpRequest(url, new HttpUtil.CallBack() {
            @Override
            public void onFinish(String response) {
                Data data = gsonData(response);
                //加载到集合
                itemList.addAll(data.getItem());
                //从网络中获取数据之后加载到数据库
                insertSQL();
                showLog();
                relist();//去除广告
                //刷新recycler
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mode ==1) {

                            adapter.refresh(itemList);
                        }
                        if(mode == 2){//上拉加载更多

                            adapter.addMoreItemBottom(itemList);

                        }
                        if(mode == 3){//下拉刷新界面

                            adapter.addMoreItemTop(itemList);
                            recyclerView.smoothScrollToPosition(0);
                            mSwipeRefreshLayout.setRefreshing(false);
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
