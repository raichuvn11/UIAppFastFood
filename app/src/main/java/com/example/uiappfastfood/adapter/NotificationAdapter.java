package com.example.uiappfastfood.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.model.NotificationItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<NotificationItem> notificationItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NotificationItem item);
    }

    public NotificationAdapter(Context context, List<NotificationItem> items, OnItemClickListener listener) {
        this.context = context;
        this.notificationItems = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = notificationItems.get(position);
        try {
            holder.ivIcon.setImageResource(item.getIconResId());
            holder.tvTitle.setText(item.getTitle());
        } catch (Resources.NotFoundException e) {
            holder.ivIcon.setImageResource(R.drawable.ic_launcher_foreground); // fallback drawable
        }
        if (!item.isRead()) {
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        } else {
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        holder.tvDescription.setText(item.getDescription());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedTime = sdf.format(new Date(item.getTimeStamp()));
        holder.tvDate.setText(formattedTime);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvDescription, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_notificationIcon);
            tvTitle = itemView.findViewById(R.id.tv_notificationTitle);
            tvDescription = itemView.findViewById(R.id.tv_notificationDescription);
            tvDate = itemView.findViewById(R.id.tv_notificationDate);
        }
    }
}

