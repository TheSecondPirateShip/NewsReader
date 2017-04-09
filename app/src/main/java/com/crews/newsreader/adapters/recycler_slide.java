package com.crews.newsreader.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crews.newsreader.R;
import com.crews.newsreader.beans.Content.Slides;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2017/4/9.
 */

public class recycler_slide extends RecyclerView.Adapter<recycler_slide.mViewHolder> {
    public interface CallBack{
        void onClick(Slides slides);
    }

    private float mwidth;
    private CallBack callBack = null;
    private List<Slides> list = new ArrayList<>();
    private Context mContext = null;

    public recycler_slide(CallBack callBack){
        this.callBack = callBack;
    }

    public void refresh(List<Slides> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_slide,parent,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        mwidth = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams lp = holder.img.getLayoutParams();
        lp.width = (int)mwidth;
        lp.height = RecyclerView.LayoutParams.WRAP_CONTENT;
        holder.img.setLayoutParams(lp);
        final Slides slides = list.get(position);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onClick(slides);
            }
        });

        holder.description.setText(slides.getDescription());
        MimageLoader.build(mContext).setBitmap(slides.getImage(),holder.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        TextView description;
        ImageView img;
        public mViewHolder(View itemView) {
            super(itemView);
            description = (TextView)itemView.findViewById(R.id.slide_description);
            img = (ImageView)itemView.findViewById(R.id.slide_img);
        }
    }
}
