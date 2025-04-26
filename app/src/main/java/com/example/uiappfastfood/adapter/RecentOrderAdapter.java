package com.example.uiappfastfood.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.activity.MenuActivity;
import com.example.uiappfastfood.model.MenuItem;
import com.example.uiappfastfood.model.RecentOrderModel;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;

import java.util.List;

public class RecentOrderAdapter extends RecyclerView.Adapter<RecentOrderAdapter.OrderViewHolder> {

    private Context context;
    private List<MenuItem> orderList;

    public RecentOrderAdapter(Context context, List<MenuItem> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        MenuItem order = orderList.get(position);
        holder.tvFoodName.setText(order.getName());
        String price = String.format("$%.2f", order.getPrice());
        holder.tvPrice.setText(price);

        Glide.with(context)
                .load(order.getImgMenuItem()) // Có thể là URL hoặc drawable (int)
                .transform(new RoundedCorners(30)) // Bo góc 30px
                .into(holder.imgFood);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MenuActivity.class);
            intent.putExtra("menu_item_id", order.getId());
            intent.putExtra("menu_item_name", order.getName());
            intent.putExtra("menu_item_price", order.getPrice());
            intent.putExtra("menu_item_desc", order.getDescription());
            intent.putExtra("menu_item_img", order.getImgMenuItem());
            intent.putExtra("menu_item_category", order.getCategoryId());
            List<Long> favoriteIds = order.getUserFavoriteIds();
            long[] favoriteIdArray = new long[favoriteIds.size()];
            for (int i = 0; i < favoriteIds.size(); i++) {
                favoriteIdArray[i] = favoriteIds.get(i);
            }

            intent.putExtra("menu_item_favorite_ids", favoriteIdArray);
            intent.putExtra("context_class_simple", context.getClass().getSimpleName());
            context.startActivity(intent);
            SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
            sharedPrefManager.saveContext(context.getClass().getSimpleName());
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }

        });

    }

    @Override
    public int getItemCount() {
        if (orderList == null) {
            return 0; // Nếu danh sách là null, trả về 0 để tránh lỗi
        }
        return orderList.size(); // Trả về kích thước của danh sách nếu không null
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvFoodName, tvPrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}
