package com.example.uiappfastfood.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.model.OrderStatus;
import java.util.List;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.OrderViewHolder> {

    private final List<OrderStatus> orderStatusList;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Long orderId);
    }

    public OrderStatusAdapter(Context context, List<OrderStatus> orderStatusList, OnItemClickListener listener) {
        this.context = context;
        this.orderStatusList = orderStatusList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_status_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderStatus orderStatus = orderStatusList.get(position);
        holder.tvOrderId.setText("Mã đơn hàng: " + orderStatus.getId().toString());
        holder.tvOrderDate.setText("Ngày đặt: " + orderStatus.getDate());
        holder.tvOrderStatus.setText(orderStatus.getStatus());
        holder.tvOrderTotal.setText("Tổng: " + String.format("%,.0fđ", orderStatus.getTotal()));

        holder.orderStatusItem.setOnClickListener(v -> {
            if (listener != null) {
                Long orderId = orderStatus.getId();
                listener.onItemClick(orderId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderStatusList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal;
        LinearLayout orderStatusItem;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_orderId);
            tvOrderDate = itemView.findViewById(R.id.tv_orderDate);
            tvOrderStatus = itemView.findViewById(R.id.tv_orderStatus);
            tvOrderTotal = itemView.findViewById(R.id.tv_orderTotal);
            orderStatusItem = itemView.findViewById(R.id.order_status_item);
        }
    }

    public void removeItemById(Long orderId) {
        int indexToRemove = -1;
        for (int i = 0; i < orderStatusList.size(); i++) {
            if (orderStatusList.get(i).getId().equals(orderId)) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove != -1) {
            orderStatusList.remove(indexToRemove);
            notifyItemRemoved(indexToRemove);
        }
    }
}

