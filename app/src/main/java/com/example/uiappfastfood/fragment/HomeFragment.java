package com.example.uiappfastfood.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.DTO.response.UserResponseDTO;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.activity.MapsActivity;
import com.example.uiappfastfood.activity.SearchActivity;
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

public class HomeFragment extends Fragment {
    private static final int MAP_REQUEST_CODE = 1234;

    private RecyclerView categoryRecyclerView, foodRecyclerView;
    private CategoryAdapter categoryAdapter;
    private MenuItemAdapter productAdapter;
    private TextView cityName;
    private List<Category> categoryList = new ArrayList<>();
    private List<MenuItem> productList = new ArrayList<>();

    private UserResponseDTO currentUser;

    private ImageView searchIcon;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView location = view.findViewById(R.id.location);
        categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        foodRecyclerView = view.findViewById(R.id.food_recycler_view);
        cityName = view.findViewById(R.id.city_name);
        searchIcon = view.findViewById(R.id.search_icon);
        NestedScrollView nestedScrollView = view.findViewById(R.id.homeFragment);
        ImageView banner = view.findViewById(R.id.banner);
        RelativeLayout relativeLayout = view.findViewById(R.id.relativeLayout);
        TextView title = view.findViewById(R.id.title);

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

        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        foodRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        categoryAdapter = new CategoryAdapter(getContext(), categoryList, category -> {
            loadFoodItems(category.getMenuItems());
        });
        categoryRecyclerView.setAdapter(categoryAdapter);

        productAdapter = new MenuItemAdapter(getContext(), productList, true);
        foodRecyclerView.setAdapter(productAdapter);

        loadCategories();
        getUserProfile();  // Gọi API lấy thông tin người dùng

        location.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            if (currentUser.getAddress() == null)
            {
                intent.putExtra("address", "null");
            }
            else intent.putExtra("address", cityName.getText().toString());
            startActivityForResult(intent, MAP_REQUEST_CODE);
        });

        return view;
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
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFoodItems(List<MenuItem> menuItems) {
        productList = menuItems;
        productAdapter.updateMenuItems(productList);
    }

    private void getUserProfile() {
        // Gọi API lấy thông tin người dùng
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        SharedPrefManager sharedPrefManager = new SharedPrefManager(getContext());
        Call<UserResponseDTO> call = apiService.getUserProfile(sharedPrefManager.getUserId());  // Thay bằng phương thức API của bạn

        call.enqueue(new Callback<UserResponseDTO>() {
            @Override
            public void onResponse(Call<UserResponseDTO> call, Response<UserResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    updateCityName();  // Cập nhật cityName khi có thông tin người dùng
                }
            }

            @Override
            public void onFailure(Call<UserResponseDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCityName() {

        if (currentUser != null) {
            String userAddress = currentUser.getAddress();
            if (userAddress == null || userAddress.isEmpty()) {
                cityName.setText("Please set Address");
            } else {
                cityName.setText(userAddress);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String selectedAddress = data.getStringExtra("selected_address");
            cityName.setText(selectedAddress);  // Cập nhật cityName với địa chỉ đã chọn từ MapsActivity
        }
    }
}
