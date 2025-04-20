package com.example.uiappfastfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.model.FavoriteItem;
import com.example.uiappfastfood.model.OrderItem;

import java.util.List;

public class FavoriteItemAdapter extends RecyclerView.Adapter<FavoriteItemAdapter.FavoriteItemViewHolder> {
    private List<FavoriteItem> favoriteItems;
    private Context context;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FavoriteItem item);
    }
    public FavoriteItemAdapter(Context context, List<FavoriteItem> favoriteItems, OnItemClickListener listener) {
        this.favoriteItems = favoriteItems;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_item, parent, false);
        return new FavoriteItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteItemViewHolder holder, int position) {
        FavoriteItem item = favoriteItems.get(position);
        Glide.with(context)
                .load(item.getImg())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_edit_avatar)
                .into(holder.ivImg);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format("%,.0fđ", item.getPrice()));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        if (favoriteItems == null) {
            return 0;
        }
        return favoriteItems.size();
    }

    public static class FavoriteItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImg;
        TextView tvName, tvPrice;
        public FavoriteItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.iv_favItemImg);
            tvName = itemView.findViewById(R.id.tv_favItemName);
            tvPrice = itemView.findViewById(R.id.tv_favItemPrice);
        }
    }

    public void filterList(List<FavoriteItem> filteredList) {
        this.favoriteItems = filteredList;
        notifyDataSetChanged();
    }

}
