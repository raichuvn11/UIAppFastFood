package com.example.uiappfastfood.adapter;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.model.RecentSearch;

import java.util.List;

public class RecentSearchAdapter extends BaseAdapter {

    private Context context;
    private List<RecentSearch> recentSearches;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    public RecentSearchAdapter(Context context, List<RecentSearch> recentSearches, OnDeleteClickListener listener) {
        this.context = context;
        this.recentSearches = recentSearches;
        this.onDeleteClickListener = listener;
    }

    @Override
    public int getCount() {
        return recentSearches.size();
    }

    @Override
    public Object getItem(int position) {
        return recentSearches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_search, parent, false);

        TextView tvKeyword = view.findViewById(R.id.tvKeyword);
        ImageView ivDelete = view.findViewById(R.id.ivDelete);

        RecentSearch recentSearch = recentSearches.get(position);
        tvKeyword.setText(recentSearch.getKeyword());  // Hiển thị từ khóa tìm kiếm

        ivDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDelete(position);
            }
        });

        return view;
    }
}
