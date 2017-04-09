package com.crews.newsreader.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.crews.newsreader.R;
import com.crews.newsreader.beans.New;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2017/4/9.
 */

public class recycler extends RecyclerView.Adapter<recycler.mViewHolder> {
    public interface CallBack{
        void onClick(New item);
    }

    private CallBack callBack = null;
    private List<New> list = new ArrayList<>();
    private Context mContext = null;

    public recycler(CallBack callBack){
        this.callBack = callBack;
    }

    public void refresh(List<New> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item,parent,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        final New item = list.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onClick(item);
            }
        });
        holder.title.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public mViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.item_title);
        }
    }
}
