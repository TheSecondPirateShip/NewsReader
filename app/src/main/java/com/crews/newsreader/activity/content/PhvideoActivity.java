package com.crews.newsreader.activity.content;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.crews.newsreader.R;
import com.crews.newsreader.beans.Main.Item;

public class PhvideoActivity extends AppCompatActivity {

    Item mItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phvideo);
        try {
            Thread thread = Thread.currentThread();
            thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        init();
    }

    private void init(){
        Intent intent = getIntent();
        mItem = (Item)intent.getSerializableExtra("new");
        Toast.makeText(this,"网络错误 网络错误", Toast.LENGTH_SHORT).show();
    }
}
