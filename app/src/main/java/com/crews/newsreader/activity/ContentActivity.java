package com.crews.newsreader.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.crews.newsreader.R;
import com.crews.newsreader.beans.New;

public class ContentActivity extends AppCompatActivity {

    New mNew = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        init();
    }

    private void init(){
        Intent intent = getIntent();
        mNew = (New)intent.getSerializableExtra("new");
        Toast.makeText(this, mNew.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
