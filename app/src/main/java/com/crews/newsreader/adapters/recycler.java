package com.crews.newsreader.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.crews.newsreader.R;
import com.crews.newsreader.activity.MainActivity;
import com.crews.newsreader.beans.Main.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2017/4/9.
 */

public class recycler extends RecyclerView.Adapter<recycler.mViewHolder> {
    public interface CallBack{
        void onClick(Item item);
    }
    public static final int TYPE_FOOTER = 0;
    public static final int TYPE_NORMAL = 1;
    private CallBack callBack = null;
    private List<Item> list = new ArrayList<>();
    private Context mContext = null;

    public recycler(CallBack callBack){
        this.callBack = callBack;
    }

    public void refresh(List<Item> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        if(viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_load_more_layout, parent, false);
            return new mViewHolder(view,TYPE_FOOTER);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_main, parent, false);
            return new mViewHolder(view,TYPE_NORMAL);
        }
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        if(holder.viewType == TYPE_NORMAL && position != 0){

            final Item item = list.get(position-1);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.onClick(item);
                }
            });
            String s = item.getType()+": "+item.getTitle();
            holder.title.setText(s);
            MimageLoader.build(mContext).setBitmap(item.getThumbnail(),holder.img);
            holder.source.setText(item.getSource() + " " + item.getUpdateTime());
            holder.comment_num.setText(item.getComments());
        }
        if(holder.viewType == TYPE_FOOTER){
            holder.mProgressBar.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(position + 1 == getItemCount()){
            return TYPE_FOOTER;
        }else {
            return TYPE_NORMAL;
        }
    }
    @Override
    public int getItemCount() {
        return list.size() + 1;
    }
    public void addMoreItem(List<Item> newDatas) {
        list.addAll(newDatas);
        notifyDataSetChanged();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView source;
        TextView comment_num;
        TextView add_more_text;
        ImageView img;
        ProgressBar mProgressBar;
        int viewType;
        public mViewHolder(View itemView,int viewType) {
            super(itemView);

            this.viewType = viewType;
            title = (TextView)itemView.findViewById(R.id.item_title_doc);
            img = (ImageView)itemView.findViewById(R.id.item_img_doc);
            source = (TextView) itemView.findViewById(R.id.item_source_doc);
            comment_num = (TextView) itemView.findViewById(R.id.item_comment_doc);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            add_more_text = (TextView) itemView.findViewById(R.id.foot_view_item_tv);
        }
    }
}
