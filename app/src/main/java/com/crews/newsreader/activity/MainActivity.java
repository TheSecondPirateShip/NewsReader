package com.crews.newsreader.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    //数据库查询信息
    private boolean search;
    private long firstTime;

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            adapter.refresh(itemList);
        }
    };

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

        setRecyclerView();
        getFromSQL();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                    Cursor cursor = sqUtil.getDb().query("News", null, null, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        Log.d(TAG,"cursor.moveToFirst()");
                        do{
                            Item item = new Item();
                            item.setUpdateTime(cursor.getString(cursor.getColumnIndex("date")));
                            Link link = new Link();
                            link.setType(cursor.getString(cursor.getColumnIndex("type")));
                            link.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                            item.setLink(link);
                            item.setCommentsUrl(cursor.getString(cursor.getColumnIndex("commentsUrl")));
                            item.setComments(cursor.getString(cursor.getColumnIndex("comments")));
                            item.setThumbnail(cursor.getString(cursor.getColumnIndex("thumbnail")));
                            item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                            itemList.add(item);
                            //Log.d(TAG,item.getUpdateTime());
                        }while (cursor.moveToNext());
                        cursor.close();
                        if(itemList != null){
                            //实在不会写回调  用hanlder
                            Message message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        }
                    }
                    else{
                        getFromHttp(1);
                        Log.d(TAG,"getFromHttp(1);");
                    }
            }
        }).start();
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
                    search = sqUtil.query(obj);
                    if(search == true){
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
                if(item.getLink().getType().equals("doc"))
                {
                    toActivity(DocActivity.class,item);
                }
                else if(item.getLink().getType().equals("slide"))
                {
                    toActivity(SlideActivity.class,item);
                }
                else if(item.getLink().getType().equals("phvideo")){
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
        itemList.clear();
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
        //接口总是抽风 得不到数据就用保存下来的
        if(response.equals("{\"listId\":\"irecommend\",\"type\":\"irecommendlist\",\"count\":0,\"item\":[]}")){
            response = "{\"listId\":\"irecommend\",\"type\":\"irecommendlist\",\"count\":15,\"item\":[{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p0.ifengimg.com\\/yidian\\/2017_15\\/06ed3824907dab5_w550_h337.jpg\",\"title\":\"朝鲜：美国“侵略”行动已达危险阶段 将坚决回击\",\"source\":\"中国经济网\",\"simId\":\"clusterId_8335104\",\"updateTime\":\"2017\\/04\\/12 07:21:45\",\"documentId\":\"imcp_121098381\",\"type\":\"doc\",\"style\":{\"type\":\"normal\"},\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15473679.shtml\",\"comments\":\"42\",\"commentsall\":\"187\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121098381\"}},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p0.ifengimg.com\\/pmop\\/2017\\/0411\\/0B5622AA01D17CD4CC03000D02ED93B4F4F74FF9_size37_w252_h253.png\",\"title\":\"常见飞机电插头大全，赶快收藏吧！\",\"source\":\"航佳技术\",\"simId\":\"clusterId_8333724\",\"updateTime\":\"2017\\/04\\/11 11:27:22\",\"documentId\":\"imcp_121057646\",\"type\":\"doc\",\"style\":{\"type\":\"normal\"},\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15400598.shtml\",\"comments\":\"7\",\"commentsall\":\"7\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121057646\"}},{\"thumbnail\":\"\",\"title\":\"4月16日起 南充火车站增开成都至达州车次\",\"source\":\"成都全搜索\",\"simId\":\"clusterId_8370984\",\"updateTime\":\"2017\\/04\\/11 22:30:24\",\"documentId\":\"imcp_121089043\",\"type\":\"doc\",\"style\":{\"type\":\"normal\"},\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15454241.shtml\",\"comments\":\"2\",\"commentsall\":\"2\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121089043\"}},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p0.ifengimg.com\\/pmop\\/2017\\/04\\/12\\/5c9d69ea-c2fd-4b53-a70e-9b5ebb4a025e.jpg\",\"title\":\"郭晶晶现身街头腹部大如球 或于月底再生一女\",\"online\":\"0\",\"phvideo\":{\"channelName\":\"飞鹰TV\",\"length\":61},\"id\":\"2b3b3aea-fdd1-4c80-bd2b-6715202a1285\",\"flag\":\"ifengvideos\",\"documentId\":\"imcp_crc_2568093666\",\"type\":\"phvideo\",\"link\":{\"type\":\"phvideo\",\"url\":\"2b3b3aea-fdd1-4c80-bd2b-6715202a1285\"},\"commentsUrl\":\"http:\\/\\/share.iclient.ifeng.com\\/sharenews.f?guid=2b3b3aea-fdd1-4c80-bd2b-6715202a1285\",\"simId\":\"clusterId_8393410\",\"comments\":\"0\",\"commentsall\":\"0\"},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p1.ifengimg.com\\/yidian\\/2017_15\\/e24cebd5b8048e1_w480_h137.jpg\",\"title\":\"达康书记侯亮平都没搞懂的这个问题，曾经绊倒过终极大boss\",\"source\":\"雪球\",\"simId\":\"clusterId_8363522\",\"updateTime\":\"2017\\/04\\/11 23:15:30\",\"documentId\":\"imcp_121093979\",\"type\":\"doc\",\"style\":{\"type\":\"normal\"},\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15460680.shtml\",\"comments\":\"0\",\"commentsall\":\"0\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121093979\"}},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w166_h118_q75\\/p3.ifengimg.com\\/haina\\/2017_15\\/5373476cd912a33_w306_h188.jpg\",\"online\":\"1\",\"title\":\"武汉人每天骑行百万次 共享单车推出准入门槛势在必行\",\"source\":\"武汉晚报\",\"updateTime\":\"2017-04-12 09:23:21\",\"type\":\"doc\",\"commentsUrl\":\"http:\\/\\/hb.ifeng.com\\/a\\/20170412\\/5563602_0.shtml\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=cmpp_087590005563602\"},\"documentId\":\"087590005563602\",\"id\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=cmpp_087590005563602\",\"simId\":\"clusterId_8380834\",\"comments\":\"0\",\"commentsall\":\"0\"},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/img1.ugc.ifeng.com\\/newugc\\/20170411\\/17\\/wemedia\\/67b2406468e5aae96402b5e55bcbfe81f4d7c4d6_size158_w800_h600.jpg\",\"title\":\"华裔医生被拖下飞机溅血晕厥 美联航CEO：他挑衅好斗\",\"source\":\"瞰天下\",\"simId\":\"clusterId_8354791\",\"updateTime\":\"2017\\/04\\/11 17:05:35\",\"documentId\":\"imcp_121084063\",\"type\":\"doc\",\"style\":{\"type\":\"normal\"},\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15442452.shtml\",\"comments\":\"8\",\"commentsall\":\"22\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121084063\"}},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p0.ifengimg.com\\/yidian\\/2017_15\\/6f107743fedc900_w640_h427.jpg\",\"title\":\"没买上雄安新区的房，但绝不能再错过这款SUV！\",\"source\":\"智选车\",\"simId\":\"clusterId_8336873\",\"updateTime\":\"2017\\/04\\/11 12:28:24\",\"documentId\":\"imcp_121065385\",\"type\":\"doc\",\"style\":{\"type\":\"normal\"},\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15411907.shtml\",\"comments\":\"5\",\"commentsall\":\"9\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121065385\"}},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p0.ifengimg.com\\/yidian\\/2017_15\\/87a7f1bac6dc6ee_w487_h341.jpg\",\"title\":\"史上最强悍的五个女人，你最佩服哪一个？\",\"source\":\"百家之言\",\"simId\":\"clusterId_8355857\",\"updateTime\":\"2017\\/04\\/11 17:18:37\",\"documentId\":\"imcp_121078912\",\"type\":\"doc\",\"style\":{\"type\":\"normal\"},\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15431763.shtml\",\"comments\":\"7\",\"commentsall\":\"9\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121078912\"}},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p3.ifengimg.com\\/yidian\\/2017_15\\/95798ed0a06d510_w600_h399.jpg\",\"title\":\"中国老板身家6亿欧被米兰看不起 赌上全部资产\",\"source\":\"三十年莱斯特城球\",\"simId\":\"clusterId_7322849\",\"updateTime\":\"2017\\/04\\/11 23:08:56\",\"documentId\":\"imcp_121089713\",\"type\":\"doc\",\"style\":{\"type\":\"normal\"},\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15456554.shtml\",\"comments\":\"2\",\"commentsall\":\"2\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121089713\"}},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p0.ifengimg.com\\/pmop\\/2017\\/0411\\/5439F83148A35CC6550F089B9E6272D4035A2C01_size43_w596_h590.jpeg\",\"title\":\"血管堵塞？多吃这些食物血管垃圾不再有\",\"source\":\"鹏尚阁养生\",\"simId\":\"clusterId_3702459\",\"updateTime\":\"2017\\/04\\/11 16:56:26\",\"documentId\":\"imcp_121078381\",\"type\":\"doc\",\"style\":{\"type\":\"normal\"},\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15429595.shtml\",\"comments\":\"8\",\"commentsall\":\"8\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121078381\"}},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p2.ifengimg.com\\/a\\/2017_15\\/61c3905f2512329_size68_w598_h598.jpg\",\"title\":\"泰国变性人征兵后要求进入男兵宿舍\",\"source\":\"凤凰网\",\"simId\":\"clusterId_8379732\",\"updateTime\":\"2017\\/04\\/12 08:18:00\",\"documentId\":\"imcp_121099710\",\"type\":\"slide\",\"style\":{\"type\":\"slides\",\"images\":[\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p2.ifengimg.com\\/a\\/2017_15\\/61c3905f2512329_size68_w598_h598.jpg\",\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p3.ifengimg.com\\/a\\/2017_15\\/0e6d906ca4a7d33_size32_w386_h598.jpg\",\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p1.ifengimg.com\\/a\\/2017_15\\/bf653ea0dfd0e04_size72_w867_h598.jpg\"],\"slideCount\":9},\"hasSlide\":true,\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15475811.shtml\",\"comments\":\"3\",\"commentsall\":\"9\",\"link\":{\"type\":\"slide\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121099710\"}},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p0.ifengimg.com\\/pmop\\/2017\\/0411\\/F0957A44E7E3D8A2912AA0B068BDACF29B68B59F_size34_w640_h360.jpeg\",\"title\":\"乌克兰曾要将这一军舰出售给中国却遭美国威逼利诱：如今沦为废铁\",\"source\":\"军事突击队\",\"simId\":\"clusterId_8361353\",\"updateTime\":\"2017\\/04\\/11 18:57:43\",\"documentId\":\"imcp_121084585\",\"type\":\"doc\",\"style\":{\"type\":\"normal\"},\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15443629.shtml\",\"comments\":\"8\",\"commentsall\":\"14\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121084585\"}},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w134_h96_q75\\/p0.ifengimg.com\\/pmop\\/2017\\/0411\\/EF264D97E52ED6E223EBCFC783487C9A8F2AD3F2_size329_w732_h410.png\",\"title\":\"公务员晋升最快的五个部门是？\",\"source\":\"广东华图\",\"simId\":\"clusterId_2694635\",\"updateTime\":\"2017\\/04\\/11 14:29:03\",\"documentId\":\"imcp_121074648\",\"type\":\"doc\",\"style\":{\"type\":\"normal\"},\"commentsUrl\":\"http:\\/\\/t.ifeng.com\\/appshare\\/15424283.shtml\",\"comments\":\"3\",\"commentsall\":\"5\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=121074648\"}},{\"thumbnail\":\"http:\\/\\/d.ifengimg.com\\/w166_h118_q75\\/p3.ifengimg.com\\/a\\/2017_15\\/93f559b15f7c282_size51_w400_h533.jpg\",\"online\":\"1\",\"title\":\"世界最大油灯博物馆将于常州西太湖畔开放\",\"source\":\"中国常州网\",\"updateTime\":\"2017-04-11 16:35:10\",\"type\":\"doc\",\"commentsUrl\":\"http:\\/\\/js.ifeng.com\\/a\\/20170411\\/5552042_0.shtml\",\"link\":{\"type\":\"doc\",\"url\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=cmpp_08BBC0005552042\"},\"documentId\":\"08BBC0005552042\",\"id\":\"http:\\/\\/api.iclient.ifeng.com\\/ipadtestdoc?aid=cmpp_08BBC0005552042\",\"simId\":\"clusterId_8128265\",\"comments\":\"0\",\"commentsall\":\"0\"}]}";
        }
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
            if (itemList.get(i).getType().equals("web")||itemList.get(i).getType().equals("phvideo")) {
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
    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();

        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            super.onBackPressed();
        }
    }

}
