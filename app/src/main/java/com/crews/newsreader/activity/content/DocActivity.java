package com.crews.newsreader.activity.content;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);
        init();
        bind();
        getFromHttp();
    }

    private void init() {
        Intent intent = getIntent();
        mItem = (Item) intent.getSerializableExtra("new");
        text = mItem.getLink().getUrl();
    }

    private void bind() {
        header = (TextView) findViewById(R.id.doc_header);
        content = (TextView) findViewById(R.id.doc_content);
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
                            }
                        });
                    }
                });
            }
        });
    }

    private Content gsonContent(String response) {
        Gson gson = new Gson();
        return gson.fromJson(response, Content.class);
    }

    private void getHTML(final Call call) {
        final float width = this.getWindowManager().getDefaultDisplay().getWidth();
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
