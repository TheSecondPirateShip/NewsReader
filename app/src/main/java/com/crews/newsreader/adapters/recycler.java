package com.crews.newsreader.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


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
    public static final int TYPE_SLIDES = 2;
    public static final int TYPE_PHVIDEO = 3;
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
        }else if (viewType == TYPE_SLIDES){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_slides,parent,false);
            return new mViewHolder(view,TYPE_SLIDES);
        }else if (viewType == TYPE_PHVIDEO){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_phvideo,parent,false);
            return new mViewHolder(view,TYPE_PHVIDEO);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_main, parent, false);
            return new mViewHolder(view,TYPE_NORMAL);
        }
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, final int position) {
        if(holder.viewType == TYPE_NORMAL && position != 0){
            final Item item = list.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.onClick(item);
                }
            });
            String s = item.getType()+": "+item.getTitle();
            holder.title_doc.setText(s);
            MimageLoader.build(mContext).setBitmap(item.getThumbnail(),holder.img_doc);
            holder.source_doc.setText(item.getSource() + " " + item.getUpdateTime());
            holder.comment_num_doc.setText(item.getComments());
            holder.img_del_doc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(list.size() >= 2) {
                        list.remove(position);
                        notifyDataSetChanged();
                    }
                    Toast.makeText(mContext,"删除此条",Toast.LENGTH_SHORT).show();
                }
            });
        }
        if( holder.viewType == TYPE_SLIDES ){
            final Item item = list.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.onClick(item);
                }
            });
            MimageLoader.build(mContext).setBitmap(item.getStyle().getImages().get(0),holder.img_slides1);
            MimageLoader.build(mContext).setBitmap(item.getStyle().getImages().get(1),holder.img_slides2);
            MimageLoader.build(mContext).setBitmap(item.getStyle().getImages().get(2),holder.img_slides3);
            holder.source_slides.setText(item.getSource() + "  " + item.getUpdateTime());
            holder.title_slides.setText(item.getTitle());
            holder.comment_num_slides.setText(item.getComments());
            holder.img_del_slides.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(list.size() >= 2) {
                        list.remove(position);
                        notifyDataSetChanged();
                    }
                    Toast.makeText(mContext,"删除此条",Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (holder.viewType == TYPE_PHVIDEO) {
            final Item item = list.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.onClick(item);
                }
            });
            holder.img_del_phvideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(list.size() >= 2) {
                        list.remove(position);
                        notifyDataSetChanged();
                    }
                    Toast.makeText(mContext,"删除此条",Toast.LENGTH_SHORT).show();
                }
            });
            MimageLoader.build(mContext).setBitmap(item.getThumbnail(),holder.img_phvideo);
            holder.comment_num_phvideo.setText(item.getComments());
            holder.source_phvideo.setText(item.getSource() + item.getUpdateTime());
            holder.title_phvideo.setText(item.getTitle());

        }
        if(holder.viewType == TYPE_FOOTER){
            holder.mProgressBar.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(position + 1 == getItemCount()){
            return TYPE_FOOTER;
        }else if (list.get(position).getLink().getType().equals("slides")) {
            return TYPE_SLIDES;
        }else if (list.get(position).getLink().getType().equals("phvideo")) {
            return TYPE_PHVIDEO;
        }else {
            return TYPE_NORMAL;
        }
    }
    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    /**
     * 上拉加载时把数据添加在下面
     * @param newDatas
     */
    public void addMoreItemBottom(List<Item> newDatas) {
        list.addAll(newDatas);
        notifyDataSetChanged();
    }

    /**
     * 下拉加载时把数据添加在上面
     * @param newDatas
     */
    public void addMoreItemTop(List<Item> newDatas){
        List<Item> temp = new ArrayList<>();
        temp.addAll(newDatas);
        temp.addAll(list);
        list = temp;
        notifyDataSetChanged();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        TextView title_doc,comment_num_doc,source_doc;
        TextView title_slides,comment_num_slides,source_slides;
        TextView title_phvideo,comment_num_phvideo,source_phvideo;
        TextView add_more_text;
        ImageView img_doc,img_del_doc;
        ImageView img_slides1,img_slides2,img_slides3,img_del_slides;
        ImageView img_phvideo,img_del_phvideo;
        ProgressBar mProgressBar;
        int viewType;
        public mViewHolder(View itemView,int viewType) {
            super(itemView);

            this.viewType = viewType;
            title_doc = (TextView)itemView.findViewById(R.id.item_title_doc);
            img_doc = (ImageView)itemView.findViewById(R.id.item_img_doc);
            source_doc = (TextView) itemView.findViewById(R.id.item_source_doc);
            comment_num_doc = (TextView) itemView.findViewById(R.id.item_comment_doc);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            add_more_text = (TextView) itemView.findViewById(R.id.foot_view_item_tv);
            img_del_doc = (ImageView) itemView.findViewById(R.id.item_del_doc);
            title_slides = (TextView) itemView.findViewById(R.id.item_title_slides);
            source_slides = (TextView) itemView.findViewById(R.id.item_source_slides);
            comment_num_slides = (TextView) itemView.findViewById(R.id.item_comment_slides);
            img_slides1 = (ImageView)itemView.findViewById(R.id.item_img_slides1);
            img_slides2 = (ImageView)itemView.findViewById(R.id.item_img_slides2);
            img_slides3 = (ImageView)itemView.findViewById(R.id.item_img_slides3);
            img_del_slides = (ImageView)itemView.findViewById(R.id.item_del_slides);
            title_phvideo = (TextView)itemView.findViewById(R.id.item_title_phvideo);
            source_phvideo = (TextView)itemView.findViewById(R.id.item_source_phvideo);
            comment_num_phvideo = (TextView)itemView.findViewById(R.id.item_comment_phvideo);
            img_phvideo = (ImageView)itemView.findViewById(R.id.item_img_phvideo);
            img_del_phvideo = (ImageView)itemView.findViewById(R.id.item_del_phvideo);

        }
    }
}
