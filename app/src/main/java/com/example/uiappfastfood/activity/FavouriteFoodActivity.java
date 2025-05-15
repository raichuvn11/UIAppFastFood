package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.FavoriteItemAdapter;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.model.FavoriteItem;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteFoodActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText searchFavorite;
    private FavoriteItemAdapter favoriteItemAdapter;
    private List<FavoriteItem> favoriteItems;
    private ApiService apiService;
    private Long userId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_food);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        userId = sharedPrefManager.getUserId();

        searchFavorite = findViewById(R.id.et_search_favorite);
        searchFavorite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFavoriteItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        recyclerView = findViewById(R.id.rv_favoriteItem);
        getFavoriteItems();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //back to previous activity
        findViewById(R.id.cvBack).setOnClickListener(v -> {
            finish();
        });
    }

    private void getFavoriteItems() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.getFavoriteItems(userId).enqueue(new Callback<List<FavoriteItem>>() {
            @Override
            public void onResponse(Call<List<FavoriteItem>> call, Response<List<FavoriteItem>> response) {
                if(response.isSuccessful() && response.body() != null){
                    favoriteItems = response.body();
                    favoriteItemAdapter = new FavoriteItemAdapter(FavouriteFoodActivity.this, favoriteItems, item -> {

                        //handle click item
                        Toast.makeText(FavouriteFoodActivity.this, "Đã click", Toast.LENGTH_SHORT).show();

                    });
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

    private void filterFavoriteItems(String query) {
        List<FavoriteItem> filteredList = new ArrayList<>();
        for (FavoriteItem item : favoriteItems) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        favoriteItemAdapter.filterList(filteredList);
    }

}
