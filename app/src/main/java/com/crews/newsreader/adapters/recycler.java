package com.crews.newsreader.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
    private PopupWindow popupWindow;

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
    public void onBindViewHolder(final mViewHolder holder, final int position) {
        Item item = null;
        if(list.size()>position) {
            item = list.get(position);
        }
        if(item != null) {
            if (holder.viewType == TYPE_NORMAL) {
                String s = item.getTitle();
                holder.title_doc.setText(s);
                MimageLoader.build(mContext).setImagePlace(R.mipmap.ic_launcher).setBitmap(item.getThumbnail(), holder.img_doc);
                holder.source_doc.setText(item.getSource() + " " + item.getUpdateTime());
                holder.comment_num_doc.setText(item.getComments());
                setIsRead(item, holder.title_doc);
                setIsRead(item, holder.source_doc);
                setIsRead(item, holder.comment_num_doc);
                //删除图标点击事件
                holder.img_del_doc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initPopupWindow(view,position);
                        Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.alpha_out);

                    }
                });
            }
            if (holder.viewType == TYPE_SLIDES) {
                MimageLoader.build(mContext).setImagePlace(R.mipmap.ic_launcher).setBitmap(item.getStyle().getImages().get(0), holder.img_slides1);
                MimageLoader.build(mContext).setImagePlace(R.mipmap.ic_launcher).setBitmap(item.getStyle().getImages().get(1), holder.img_slides2);
                MimageLoader.build(mContext).setImagePlace(R.mipmap.ic_launcher).setBitmap(item.getStyle().getImages().get(2), holder.img_slides3);
                holder.source_slides.setText(item.getSource() + "  " + item.getUpdateTime());
                holder.title_slides.setText(item.getTitle());
                holder.comment_num_slides.setText(item.getComments());
                setIsRead(item, holder.title_slides);
                setIsRead(item, holder.source_slides);
                setIsRead(item, holder.comment_num_slides);
                //删除图标点击事件
                holder.img_del_slides.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initPopupWindow(view , position);
                    }
                });
            }
            if (holder.viewType == TYPE_PHVIDEO) {
                MimageLoader.build(mContext).setImagePlace(R.mipmap.ic_launcher).setBitmap(item.getThumbnail(), holder.img_phvideo);
                holder.comment_num_phvideo.setText(item.getComments());
                holder.source_phvideo.setText(item.getSource() +""+ item.getUpdateTime());
                holder.title_phvideo.setText(item.getTitle());
                setIsRead(item, holder.comment_num_phvideo);
                setIsRead(item, holder.source_phvideo);
                setIsRead(item, holder.title_phvideo);
                //删除图标点击事件
                holder.img_del_phvideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initPopupWindow(view , position);

                    }
                });
            }
            if (holder.viewType == TYPE_FOOTER) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
            }
            if(holder.viewType != TYPE_FOOTER) {
                final Item i = list.get(position);
                //item的点击事件，在MainActivity中更改isRead，在这里刷新recycler
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callBack.onClick(i);
                        if (holder.viewType == TYPE_SLIDES){
                            holder.title_slides.setTextColor(Color.parseColor("#BFBFBF"));
                            holder.comment_num_slides.setTextColor(Color.parseColor("#BFBFBF"));
                            holder.source_slides.setTextColor(Color.parseColor("#BFBFBF"));
                        }
                        if(holder.viewType == TYPE_NORMAL){
                            holder.title_doc.setTextColor(Color.parseColor("#BFBFBF"));
                            holder.comment_num_doc.setTextColor(Color.parseColor("#BFBFBF"));
                            holder.source_doc.setTextColor(Color.parseColor("#BFBFBF"));
                        }
                        if (holder.viewType == TYPE_PHVIDEO){
                            holder.title_phvideo.setTextColor(Color.parseColor("#BFBFBF"));
                            holder.comment_num_phvideo.setTextColor(Color.parseColor("#BFBFBF"));
                            holder.source_phvideo.setTextColor(Color.parseColor("#BFBFBF"));
                        }
                    }
                });

            }
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(position + 1 == getItemCount()){
            return TYPE_FOOTER;
        }else if (list.get(position).getLink().getType().equals("slide")) {
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

    private void setIsRead(Item item,TextView textView){
        if(item.getIsRead() == 1){
            textView.setTextColor(Color.parseColor("#BFBFBF"));
        }
        else {
            textView.setTextColor(Color.parseColor("#000000"));
        }
    }

    private void initPopupWindow(View view , int position) {
        if(popupWindow == null){
            View popupView = LayoutInflater.from(mContext).inflate(R.layout.remove_pop,null);
            // 三部曲第二  构造函数关联
            popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
            initClick(popupView , position);

        }
        // =======  两者结合才能让popup点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // =======  两者结合才能让popup点击外部消失
        // 让popup占有优先于activity的交互响应能力，不单单是焦点问题。
        popupWindow.setFocusable(true);
        // 设置动画  这里选用系统提供的
        popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        // popup和软键盘的关系
        // 三部曲第三   展示popup
        popupWindow.showAsDropDown(view,-280,-170);

    }

    private void initClick(View popupView ,final int position){
        Button button = (Button)popupView.findViewById(R.id.btn_popup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.size() >= 2) {
                    list.remove(position);
                    notifyItemRemoved(position);
                }
                Toast.makeText(mContext,"删除此条",Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
    }
}
