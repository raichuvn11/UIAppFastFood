package com.example.uiappfastfood.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.FavoriteItemAdapter;
import com.example.uiappfastfood.config.RetrofitClient;
import com.example.uiappfastfood.model.FavoriteItem;
import com.example.uiappfastfood.service.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteFoodActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FavoriteItemAdapter favoriteItemAdapter;
    private List<FavoriteItem> favoriteItems;
    private ApiService apiService;
    private Long userId = 1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_food);

        recyclerView = findViewById(R.id.rv_favoriteItem);
        getFavoriteItems();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //back to previous activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            finish();
        });
    }

    private void getFavoriteItems() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getFavoriteItems(userId).enqueue(new Callback<List<FavoriteItem>>() {
            @Override
            public void onResponse(Call<List<FavoriteItem>> call, Response<List<FavoriteItem>> response) {
                if(response.isSuccessful() && response.body() != null){
                    favoriteItems = response.body();
                    favoriteItemAdapter = new FavoriteItemAdapter(FavouriteFoodActivity.this, favoriteItems);
                    recyclerView.setAdapter(favoriteItemAdapter);
                    if(favoriteItems.isEmpty()){
                        LinearLayout emptyLayout = findViewById(R.id.layout_empty);
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                }else{
                    Toast.makeText(FavouriteFoodActivity.this, "danh sách trống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteItem>> call, Throwable t) {
                Log.e("FavouriteFoodActivity", "API Error: " + t.getMessage());
            }
        });
    }
}
