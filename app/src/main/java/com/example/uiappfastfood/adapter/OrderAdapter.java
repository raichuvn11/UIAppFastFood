package com.example.uiappfastfood.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.model.OrderItem;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    private Context context;
    private List<OrderItem> orderItems;

    public OrderAdapter(Context context, List<OrderItem> orderItemList) {
        this.context = context;
        this.orderItems = orderItemList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        Glide.with(context)
                .load(orderItem.getImg())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_edit_avatar)
                .into(holder.ivImg);
        holder.tvName.setText(orderItem.getName());
        holder.tvPrice.setText(String.format("%,.0fđ", orderItem.getPrice()));
        holder.tvCount.setText(String.valueOf(orderItem.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImg;
        TextView tvName, tvPrice, tvCount;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.iv_orderItemImg);
            tvName = itemView.findViewById(R.id.tv_orderItemName);
            tvPrice = itemView.findViewById(R.id.tv_orderItemPrice);
            tvCount = itemView.findViewById(R.id.tv_orderItemCount);
        }
    }
}
