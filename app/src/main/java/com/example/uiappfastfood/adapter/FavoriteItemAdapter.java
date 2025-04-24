package com.example.uiappfastfood.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.uiappfastfood.activity.MenuActivity;
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
        // thêm logic lấy MenuItem từ id

        //

        Glide.with(context)
                .load(item.getImg())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_edit_avatar)
                .into(holder.ivImg);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format("%,.0fđ", item.getPrice()));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));

//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, MenuActivity.class);
//            intent.putExtra("menu_item_id", menuItem.getId());
//            intent.putExtra("menu_item_name", menuItem.getName());
//            intent.putExtra("menu_item_price", menuItem.getPrice());
//            intent.putExtra("menu_item_desc", menuItem.getDescription());
//            intent.putExtra("menu_item_img", menuItem.getImgMenuItem());
//            intent.putExtra("menu_item_category", menuItem.getCategoryId());
//            List<Long> favoriteIds = menuItem.getUserFavoriteIds();
//            long[] favoriteIdArray = new long[favoriteIds.size()];
//            for (int i = 0; i < favoriteIds.size(); i++) {
//                favoriteIdArray[i] = favoriteIds.get(i);
//            }
//
//            intent.putExtra("menu_item_favorite_ids", favoriteIdArray);
//            intent.putExtra("context_class_simple", context.getClass().getSimpleName());
//            context.startActivity(intent);
//            if (context instanceof Activity) {
//                ((Activity) context).finish();
//            }
//
//        });
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
