package com.example.uiappfastfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.NotificationAdapter;
import com.example.uiappfastfood.model.NotificationItem;
import com.example.uiappfastfood.util.NotificationUtil;
import com.example.uiappfastfood.activity.OrderStatusActivity;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<NotificationItem> notificationItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = rootView.findViewById(R.id.rv_notification);

        // Lấy danh sách notification
        notificationItemList = NotificationUtil.getNotificationList(requireContext());
        Collections.sort(notificationItemList, (a, b) -> Long.compare(b.getTimeStamp(), a.getTimeStamp()));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        notificationAdapter = new NotificationAdapter(requireContext(), notificationItemList, item -> {

            int position = notificationItemList.indexOf(item);
            if (!item.isRead()) {
                item.setRead(true);
                NotificationUtil.updateNotificationStatus(requireContext(), item);
                notificationAdapter.notifyItemChanged(position);
            }

            String body = item.getDescription();
            int selectedTab = 0;

            if (body.contains("đang giao")) {
                selectedTab = 1;
            } else if (body.contains("đã giao")) {
                selectedTab = 2;
            } else if (body.contains("hủy")) {
                selectedTab = 3;
            }

            Intent intent = new Intent(requireContext(), OrderStatusActivity.class);
            intent.putExtra("selectedTab", selectedTab);
            startActivity(intent);
        });

        recyclerView.setAdapter(notificationAdapter);

        return rootView;
    }
}

