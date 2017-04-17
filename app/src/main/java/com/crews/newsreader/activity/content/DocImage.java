package com.crews.newsreader.activity.content;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crews.newsreader.R;
import com.crews.newsreader.adapters.MimageLoader;

import java.util.ArrayList;
import java.util.List;

public class DocImage extends AppCompatActivity {

    List<String> list = new ArrayList<>();
    private ViewPager viewPager;
    private TextView title;
    private TextView mSource;
    private String source;
    private TextView page;
    private TextView page_total;
    private Animation alpha_out;
    private Animation alpha_in;
    private boolean isImgClick = true ;
    private LinearLayout linearLayout;
    private List<View> viewList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_image);
        init();
        bind();
        setViewPager();
    }

    private void init(){
        Intent intent = getIntent();
        list = (List<String>) intent.getSerializableExtra("list");
        for (String s : list) {
            View view = getLayoutInflater().from(DocImage.this).inflate(R.layout.pager_img,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.pager_img);
            MimageLoader.build(DocImage.this).setBitmap(s,imageView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("000", "onClick: 单击图片");
                    if (isImgClick) {
                        linearLayout.startAnimation(alpha_out);
                        isImgClick = false;
                    }else {
                        linearLayout.startAnimation(alpha_in);
                        isImgClick = true;
                    }
                }
            });
            viewList.add(view);
        }
    }

    private void bind(){
        viewPager = (ViewPager) findViewById(R.id.viewpager_slide_doc);
        title = (TextView) findViewById(R.id.title_slide_activity_doc);
        mSource = (TextView) findViewById(R.id.source_content_slide_doc);
        page = (TextView) findViewById(R.id.page_slide_doc);
        page_total = (TextView) findViewById(R.id.page_total_slide_doc);
        linearLayout = (LinearLayout)findViewById(R.id.slide_tx_doc);
        alpha_out = AnimationUtils.loadAnimation(DocImage.this,R.anim.alpha_out);
        alpha_in = AnimationUtils.loadAnimation(DocImage.this,R.anim.alpha_in);
    }

    private void setViewPager(){
        viewPager.setAdapter(pagerAdapter);
        page_total.setText("/" + list.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                page.setText(position+1+"");
                if (isImgClick == false){
                    linearLayout.startAnimation(alpha_in);
                    isImgClick = true;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//                            super.destroyItem(container, position, object);
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    };
}
