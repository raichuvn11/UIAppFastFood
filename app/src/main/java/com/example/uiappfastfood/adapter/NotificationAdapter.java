package com.example.uiappfastfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.model.NotificationItem;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {

    private Context context;
    private List<NotificationItem> notificationItems;

    public NotificationAdapter(Context context, List<NotificationItem> notificationItems) {
        this.context = context;
        this.notificationItems = notificationItems;
    }

    @Override
    public int getCount() {
        return notificationItems.size();
    }

    @Override
    public Object getItem(int i) {
        return notificationItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Gán dữ liệu
        NotificationItem notificationItem = notificationItems.get(i);
        holder.ivIcon.setImageResource(notificationItem.getIconResId());
        holder.tvTitle.setText(notificationItem.getTitle());
        holder.tvDescription.setText(notificationItem.getDescription());

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvDescription;

        ViewHolder(View view) {
            ivIcon = view.findViewById(R.id.iv_notificationIcon);
            tvTitle = view.findViewById(R.id.tv_notificationTitle);
            tvDescription = view.findViewById(R.id.tv_notificationDescription);
        }
    }

}
