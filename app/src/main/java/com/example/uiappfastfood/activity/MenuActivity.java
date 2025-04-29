package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiappfastfood.DTO.request.AddToCartRequest;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.MenuItemAdapter;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.model.MenuItem; // Ensure this import is correct
import com.example.uiappfastfood.sharePreference.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    private ImageView productImage, btnBack;
    private TextView productName, productPrice, productDesc;
    private RecyclerView relatedProductsList;
    private MenuItemAdapter menuItemAdapter;
    private ImageView favoriteIcon;


    private long userId;
    private MenuItem currentMenuItem;

    private int categoryId; // Get this value from the Intent
    private String currentProductName; // Store the name of the current product

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_menu);

        // Initialize views
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productDesc = findViewById(R.id.product_description);
        relatedProductsList = findViewById(R.id.related_products_list);
        btnBack = findViewById(R.id.btn_back);
        favoriteIcon = findViewById(R.id.btn_favorite);

        Button addToCartButton = findViewById(R.id.btn_add_to_cart);
        addToCartButton.setOnClickListener(v -> addToCart(currentMenuItem));



        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        userId = sharedPrefManager.getUserId();




        // Get data from Intent
        Intent intent = getIntent();
        int itemId = intent.getIntExtra("menu_item_id", 0);
        String name = intent.getStringExtra("menu_item_name");
        double price = intent.getDoubleExtra("menu_item_price", 0.0);
        String desc = intent.getStringExtra("menu_item_desc");
        String imgUrl = intent.getStringExtra("menu_item_img");
        categoryId = intent.getIntExtra("menu_item_category", 0);

        long[] favoriteIdArray = intent.getLongArrayExtra("menu_item_favorite_ids");
        List<Long> favoriteIds = new ArrayList<>();
        if (favoriteIdArray != null) {
            for (long id : favoriteIdArray) {
                favoriteIds.add(id);
            }
        }

        currentMenuItem = new MenuItem();
        currentMenuItem.setName(name);
        currentMenuItem.setPrice(price);
        currentMenuItem.setDescription(desc);
        currentMenuItem.setImgMenuItem(imgUrl);
        currentMenuItem.setCategoryId(categoryId);
        currentMenuItem.setId(itemId);
        currentMenuItem.setUserFavoriteIds(favoriteIds);

        // Set data to views
        currentProductName = name; // Save the current product name
        productName.setText(name);
        productPrice.setText(String.format("%,.0fđ", price));
        productDesc.setText(desc);
        Glide.with(this).load(imgUrl).into(productImage);

        updateFavoriteIcon(currentMenuItem);


        // Set up RecyclerView for related products (Horizontal layout)
        relatedProductsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        menuItemAdapter = new MenuItemAdapter(this, new ArrayList<>(), false); // Pass an empty list and a flag
        relatedProductsList.setAdapter(menuItemAdapter);

        //Favorite button
        favoriteIcon.setOnClickListener(v -> toggleFavorite(currentMenuItem));
        String contextName = getIntent().getStringExtra("context_class_simple");
        btnBack.setOnClickListener(v -> {
            if (contextName != null && contextName.equals("SearchActivity")) {
                Intent intent1 = new Intent(MenuActivity.this, SearchActivity.class);
                startActivity(intent1);
                finish();
                return;
            }
            else if (contextName.equals("MainActivity"))
            {
                Intent intent1 = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
            }
            else
            {
                Intent intent1 = new Intent(MenuActivity.this, GuestActivity.class);
                startActivity(intent1);
                finish();
            }


        });

        // Fetch menu items for the selected category
        fetchMenuItemsByCategory(categoryId);
    }

    private void updateFavoriteIcon(MenuItem menuItem) {
        boolean isFavorite = menuItem.getUserFavoriteIds() != null && menuItem.getUserFavoriteIds().contains(userId);
        int iconRes = isFavorite ? R.drawable.ic_favorite_fill : R.drawable.ic_favorite_border;
        favoriteIcon.setImageResource(iconRes);
    }

    private void addToCart(MenuItem menuItem) {
        if (userId == -1) {
            Toast.makeText(this, "Please log in to add to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert primitives to wrapper classes
        Long userLongId = Long.valueOf(userId);   // Convert long to Long
        Long menuItemLongId = Long.valueOf(menuItem.getId()); // Convert int to Long

        // Create request object for API call
        AddToCartRequest request = new AddToCartRequest(userLongId, menuItemLongId);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.addToCart(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    boolean success = (boolean) responseBody.get("success");
                    String message = (String) responseBody.get("message");

                    // Show toast based on response
                    Toast.makeText(MenuActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MenuActivity.this, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFavorite(MenuItem menuItem) {
        if (userId == -1) {
            Toast.makeText(this, "Please log in to add to favorites", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        if (menuItem.getUserFavoriteIds().contains(userId)) {
            apiService.removeFavoriteItem(userId, menuItem.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        menuItem.getUserFavoriteIds().remove(userId);
                        updateFavoriteIcon(menuItem);
                        Toast.makeText(MenuActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(MenuActivity.this, "Failed to remove favorite", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            apiService.addFavoriteItem(userId, menuItem.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        menuItem.getUserFavoriteIds().add(userId);
                        updateFavoriteIcon(menuItem);
                        Toast.makeText(MenuActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(MenuActivity.this, "Failed to add favorite", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void fetchMenuItemsByCategory(int categoryId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.getMenuItemsByCategoryId(categoryId).enqueue(new Callback<List<MenuItem>>() { // Corrected type
            @Override
            public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MenuItem> menuItems = response.body();
                    if (menuItems != null && !menuItems.isEmpty()) {
                        // Remove the current product from the list
                        List<MenuItem> relatedProducts = new ArrayList<>();
                        for (MenuItem menuItem : menuItems) {
                            if (!menuItem.getName().equals(currentProductName)) {
                                relatedProducts.add(menuItem);
                            }
                        }

                        // Update the adapter with the filtered list
                        menuItemAdapter.updateMenuItems(relatedProducts);
                    } else {
                        Toast.makeText(MenuActivity.this, "No related products found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("MenuActivity", "Failed to load menu items: " + response.message());
                    Toast.makeText(MenuActivity.this, "Failed to load menu items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}