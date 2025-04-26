package com.example.uiappfastfood.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.DTO.response.UserResponseDTO;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.CategoryAdapter;
import com.example.uiappfastfood.adapter.MenuItemAdapter;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.model.Category;
import com.example.uiappfastfood.model.MenuItem;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuestActivity extends AppCompatActivity {


    private RecyclerView categoryRecyclerView, foodRecyclerView;
    private CategoryAdapter categoryAdapter;
    private MenuItemAdapter productAdapter;
    private List<Category> categoryList = new ArrayList<>();
    private List<MenuItem> productList = new ArrayList<>();

    private NestedScrollView nestedScrollView;

    private ImageView searchIcon,backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        foodRecyclerView = findViewById(R.id.food_recycler_view);
        searchIcon = findViewById(R.id.search_icon);
        backIcon = findViewById(R.id.back_icon);
        nestedScrollView = findViewById(R.id.homeFragment);

        ImageView banner = findViewById(R.id.banner);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        TextView title = findViewById(R.id.title);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                float fadeHeight = 600f;
                float alpha = 1f - Math.min(1f, scrollY / fadeHeight);

                banner.setAlpha(alpha);
                relativeLayout.setAlpha(alpha);
                title.setAlpha(alpha);
            }
        });

        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(GuestActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(GuestActivity.this, SearchActivity.class);
            startActivity(intent);
            finish();
        });

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        foodRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        categoryAdapter = new CategoryAdapter(this, categoryList, category -> {
            loadFoodItems(category.getMenuItems());
        });
        categoryRecyclerView.setAdapter(categoryAdapter);

        productAdapter = new MenuItemAdapter(this, productList, true);
        foodRecyclerView.setAdapter(productAdapter);

        loadCategories();
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        sharedPrefManager.clearUserId();

    }

    private void loadCategories() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Category>> call = apiService.getAllCategories();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    categoryAdapter.updateCategories(categoryList);

                    if (!categoryList.isEmpty()) {
                        categoryAdapter.selectCategory(0);
                        loadFoodItems(categoryList.get(0).getMenuItems());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(GuestActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFoodItems(List<MenuItem> menuItems) {
        productList = menuItems;
        productAdapter.updateMenuItems(productList);
    }


}
