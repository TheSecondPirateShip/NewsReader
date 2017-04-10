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

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private float mwidth;
    private String header;
    private CallBack callBack = null;
    private List<Slides> list = new ArrayList<>();
    private Context mContext = null;

    public recycler_slide(CallBack callBack,String header){
        this.callBack = callBack;
        this.header = header;
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
        recycler_slide.mViewHolder holder;
        if(viewType == TYPE_HEADER)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.slide_header,parent,false);
            holder = new mViewHolder(view,TYPE_HEADER);
        }
        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_slide,parent,false);
            holder = new mViewHolder(view,TYPE_NORMAL);
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        if(holder.viewType == TYPE_NORMAL && position != 0){
            final Slides slides = list.get(position-1);
            mwidth = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
            ViewGroup.LayoutParams lp = holder.img.getLayoutParams();
            lp.width = (int)mwidth;
            lp.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            holder.img.setLayoutParams(lp);
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.onClick(slides);
                }
            });
            holder.description.setText(slides.getDescription());
            MimageLoader.build(mContext).setBitmap(slides.getImage(),holder.img);
        }
        if(holder.viewType == TYPE_HEADER && position == 0){
            holder.header.setText(header);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        TextView description,header;
        ImageView img;
        int viewType;
        public mViewHolder(View itemView,int viewType) {
            super(itemView);
            this.viewType = viewType;
            if(viewType == TYPE_HEADER){
                header = (TextView)itemView.findViewById(R.id.slide_header);
            }
            if(viewType == TYPE_NORMAL) {
                description = (TextView) itemView.findViewById(R.id.slide_description);
                img = (ImageView) itemView.findViewById(R.id.slide_img);
            }
        }
    }
}
