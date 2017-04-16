package com.crews.newsreader.activity.content;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crews.newsreader.R;
import com.crews.newsreader.adapters.MimageLoader;
import com.crews.newsreader.beans.Content.Content;
import com.crews.newsreader.beans.Content.Slides;
import com.crews.newsreader.beans.Main.Item;
import com.crews.newsreader.utils.HttpUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SlideActivity extends AppCompatActivity {
    /**
     * 问题：目前使用scrollview嵌套recyclerView
     * 解决：用header
     */
    private Item mItem;
    private ViewPager viewPager;
    private String url;
    private TextView title;
    private TextView mSource;
    private String source;
    private TextView page;
    private TextView description;
    private TextView page_total;
    private List<View> viewList = new ArrayList<>();
    private List<Slides> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        bind();
        init();

        getFromHttp();

    }

    private void bind(){
        viewPager = (ViewPager) findViewById(R.id.viewpager_slide);
        title = (TextView) findViewById(R.id.title_slide_activity);
        mSource = (TextView) findViewById(R.id.source_content_slide);
        page = (TextView) findViewById(R.id.page_slide);
        page_total = (TextView) findViewById(R.id.page_total_slide);

        description = (TextView) findViewById(R.id.description);

    }


    private void getFromHttp(){
        HttpUtil.sendHttpRequest(url, new HttpUtil.CallBack() {
            @Override
            public void onFinish(String response) {
                list = gsonContent(response).getBody().getSlides();
                source = gsonContent(response).getBody().getSource();

                for (Slides slides : list){
                    Log.d("456456", "onCreate: "+slides.getImage());
                    View view = getLayoutInflater().from(SlideActivity.this).inflate(R.layout.pager_img,null);
                    ImageView imageView = (ImageView) view.findViewById(R.id.pager_img);
                    MimageLoader.build(SlideActivity.this).setBitmap(slides.getImage(),imageView);
                    viewList.add(view);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        viewPager.setAdapter(pagerAdapter);
//                        String s = list.size()+"";
                        page_total.setText("/" + list.size());
                        mSource.setText(source);
                        description.setText(list.get(0).getDescription());
                        title.setText(list.get(0).getTitle());
                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            }

                            @Override
                            public void onPageSelected(int position) {
                                page.setText(position+1+"");
                                title.setText(list.get(position).getTitle());
                                description.setText(list.get(position).getDescription());
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

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

    private void init(){
        Intent intent = getIntent();
        mItem = (Item)intent.getSerializableExtra("new");
        url = mItem.getLink().getUrl();
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
