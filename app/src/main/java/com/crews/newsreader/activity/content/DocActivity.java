package com.crews.newsreader.activity.content;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.crews.newsreader.R;
import com.crews.newsreader.beans.Content.Body;
import com.crews.newsreader.beans.Content.Content;
import com.crews.newsreader.beans.Main.New;
import com.crews.newsreader.utils.HttpUtil;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.URL;

public class DocActivity extends AppCompatActivity {
    private interface Call{
        void onFinish(Spanned spanned);
    }
    private New mNew = null;
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
        mNew = (New) intent.getSerializableExtra("new");
        text = mNew.getLink().getUrl();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                //清除原内容中的反斜杠
                text = text.replaceAll("\\\\", "");
                Log.d("DocTest", text);
                Spanned sp = Html.fromHtml(text, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        InputStream is = null;
                        try {
                            is = (InputStream) new URL(source).getContent();
                            Drawable d = Drawable.createFromStream(is, "src");
                            d.setBounds(0, 0, d.getIntrinsicWidth(),
                                    d.getIntrinsicHeight());
                            is.close();
                            return d;
                        } catch (Exception e) {
                            return null;
                        }
                    }
                }, null);
                call.onFinish(sp);
            }
        }).start();
    }

}
