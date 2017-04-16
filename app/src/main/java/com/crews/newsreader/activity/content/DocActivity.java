package com.crews.newsreader.activity.content;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.crews.newsreader.R;
import com.crews.newsreader.adapters.MimageLoader;
import com.crews.newsreader.beans.Content.Body;
import com.crews.newsreader.beans.Content.Content;
import com.crews.newsreader.beans.Main.Item;
import com.crews.newsreader.utils.HttpUtil;
import com.crews.newsreader.utils.ImgAdapter;
import com.google.gson.Gson;

public class DocActivity extends AppCompatActivity {
    private interface Call{
        void onFinish(Spanned spanned);
    }
    private Item mItem = null;
    private Body body = null;
    private String text = null;
    private String title = null;
    private TextView header, content;
    private long firstTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);
        init();
        bind();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setToolBar();
        }
        getFromHttp();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setToolBar(){
        Window window = this.getWindow();
//设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//设置状态栏颜色
        window.setStatusBarColor(Color.parseColor("#00000000"));
        window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        ViewGroup mContentView = (ViewGroup) this.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }
    }

    private void init() {
        Intent intent = getIntent();
        mItem = (Item) intent.getSerializableExtra("new");
        text = mItem.getLink().getUrl();
    }

    private void bind() {
        header = (TextView) findViewById(R.id.doc_header);
        content = (TextView) findViewById(R.id.doc_content);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long secondTime = System.currentTimeMillis();
                if (secondTime -  firstTime > 300) {
                    firstTime = secondTime;
                } else {
                    onBackPressed();
                }
            }
        });
    }

    private void getFromHttp() {
        HttpUtil.sendHttpRequest(text, new HttpUtil.CallBack() {
            @Override
            public void onFinish(String response) {
                body = gsonContent(response).getBody();
                text = body.getText();
                title = body.getTitle();
                getHTML(new Call() {
                    @Override
                    public void onFinish(final Spanned spanned) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                header.setText(title);
                                content.setText(spanned);
                                content.setLinksClickable(true);
                            }
                        });
                    }
                });
            }
        });
        /*try {
            Thread thread = Thread.currentThread();
            thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Toast.makeText(DocActivity.this,"双击返回",Toast.LENGTH_SHORT).show();

    }

    private Content gsonContent(String response) {
        Gson gson = new Gson();
        return gson.fromJson(response, Content.class);
    }

    private void getHTML(final Call call) {
        //final float width = this.getWindowManager().getDefaultDisplay().getWidth();
        final float width = content.getWidth()*(float)0.85;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //清除原内容中的反斜杠
                text = text.replaceAll("\\\\", "");
                Log.d("DocTest", text);
                Spanned sp = Html.fromHtml(text, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        //计算宽和高来自适应屏幕
                        Bitmap bitmap = MimageLoader.build(DocActivity.this).loadBitmapFromHttp(source, 0, 0);
                        Drawable d = new BitmapDrawable(bitmap);
                        d.setBounds(0, 0, (int) width,
                                (int) ImgAdapter.getHight(width,bitmap));
                        return d;
                    }
                }, null);
                call.onFinish(sp);
            }
        }).start();
    }
}
