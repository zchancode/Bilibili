package com.example.bilibili.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.R;
import com.example.bilibili.bean.RecentLookUser;

import java.util.List;

public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.MyViewHolder> {

    private List<RecentLookUser> list;

    public DynamicAdapter(List<RecentLookUser> list) {
        this.list = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout linearLayout;
        public MyViewHolder(ConstraintLayout v) {
            super(v);
            linearLayout = v;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(com.example.view.R.layout.dynamic_list_recycle_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView viewById = (TextView) holder.linearLayout.findViewById(com.example.view.R.id.item_text);
        viewById.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
