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
        init();
    }

    private void init(){
        Intent intent = getIntent();
        mItem = (Item)intent.getSerializableExtra("new");
        Toast.makeText(this, "phvideo"+ mItem.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
