package com.example.uiappfastfood.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.activity.OrderDetailActivity;
import com.example.uiappfastfood.adapter.OrderStatusAdapter;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.model.OrderStatus;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String status;
    private RecyclerView recyclerView;
    private ApiService apiService;
    private List<OrderStatus> orderStatusList = new ArrayList<>();
    private OrderStatusAdapter adapter;
    private ActivityResultLauncher<Intent> detailLauncher;
    public static OrderListFragment newInstance(String status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }

        detailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            long deletedOrderId = result.getData().getLongExtra("deletedOrderId", -1);
                            if (deletedOrderId != -1) {
                                removeOrderFromList(deletedOrderId);
                            }
                        }
                    }
                }
        );
    }

    private void removeOrderFromList(long orderId) {
        Iterator<OrderStatus> iterator = orderStatusList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == orderId) {
                iterator.remove();
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        getOrdersByStatus(status);
        return view;
    }

    private void getOrdersByStatus(String status) {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
        Long userId = sharedPrefManager.getUserId();
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.getOrdersByStatus(status, userId).enqueue(new Callback<List<OrderStatus>>() {
            @Override
            public void onResponse(Call<List<OrderStatus>> call, Response<List<OrderStatus>> response) {
                if (response.isSuccessful() && response.body() != null){
                    orderStatusList = response.body();
                    adapter = new OrderStatusAdapter(OrderListFragment.this.getContext(), orderStatusList, orderId -> {
                        Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                        intent.putExtra("orderId", orderId);
                        detailLauncher.launch(intent);
                    });
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<OrderStatus>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


