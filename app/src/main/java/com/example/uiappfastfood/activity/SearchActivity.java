package com.example.uiappfastfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.DTO.request.RecentSearchRequest;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.adapter.MenuItemAdapter;
import com.example.uiappfastfood.adapter.RecentOrderAdapter;
import com.example.uiappfastfood.adapter.RecentSearchAdapter;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.model.Category;
import com.example.uiappfastfood.model.MenuItem;
import com.example.uiappfastfood.model.RecentOrderModel;
import com.example.uiappfastfood.model.RecentSearch;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ListView lvRecentSearch;
    private TextView tvDeleteAll;
    private RecyclerView lvRecentOrders;
    private RecyclerView lvSearchResults; // RecyclerView for search results
    private RecentOrderAdapter orderAdapter;
    private List<MenuItem> recentOrders;
    private List<MenuItem> searchResults; // List to store search results
    private DrawerLayout drawerLayout;
    private ImageView btnFilter;
    private ImageView btnClearSearch, btnSearch;
    private TextView  tvheaderCategory;

    private ImageView btnBack;

    private LinearLayout recentSearchesHeader;
    private FrameLayout frameLayoutRecentOrders;
    private ApiService apiService;
    private List<MenuItem> allMenuItems = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    private List<RecentSearch> recentList = new ArrayList<>();

    private int selectedCategoryId;
    private long userId;
    private RecentSearchAdapter recentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        anhXa();

        fetchAllMenuItems();


        if (userId != -1) {
            fetchRecentOrders();
            getRecentSearches();
            Log.e("userId", "userId: " + userId);
        } else {
            hideRecentSearchAndOrders();
        }

        tvDeleteAll.setOnClickListener(v -> clearAllRecentSearches());

        btnBack.setOnClickListener(v -> {
            if (userId != -1)
            {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent = new Intent(SearchActivity.this, GuestActivity.class);
                startActivity(intent);
                finish();
            }

        });

        btnFilter.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.LEFT));
        spinnerSetup();
        search();
        applyFilters();
    }

    private void anhXa() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        userId = sharedPrefManager.getUserId();

        etSearch = findViewById(R.id.etSearch);
        lvRecentSearch = findViewById(R.id.lvRecentSearch);
        tvDeleteAll = findViewById(R.id.tvDeleteAll);
        lvRecentOrders = findViewById(R.id.lvRecentOrders);
        lvSearchResults = findViewById(R.id.lvSearchResults);
        drawerLayout = findViewById(R.id.drawerLayout);
        btnFilter = findViewById(R.id.btnFilter);
        tvheaderCategory = findViewById(R.id.tvheaderCategory);
        recentSearchesHeader = findViewById(R.id.recentSearchesHeader);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        btnSearch = findViewById(R.id.btnSearch);
        frameLayoutRecentOrders = findViewById(R.id.frameLayoutRecentOrders);
        btnBack = findViewById(R.id.btnBack);

        // Nếu user chưa đăng nhập thì không cần thiết lập Recent Adapter và ẩn layout
        if (userId != -1) {
            recentList = new ArrayList<>();
            recentAdapter = new RecentSearchAdapter(this, recentList, position -> deleteSearch(recentList.get(position).getKeyword()));
            lvRecentSearch.setAdapter(recentAdapter);

            recentOrders = new ArrayList<>();
            orderAdapter = new RecentOrderAdapter(this, recentOrders);
            lvRecentOrders.setLayoutManager(new LinearLayoutManager(this));
            lvRecentOrders.setAdapter(orderAdapter);

            lvRecentSearch.setOnItemClickListener((parent, view, position, id) -> {
                RecentSearch rs = recentList.get(position);
                String keyword = rs.getKeyword();
                etSearch.setText(keyword);
                performSearch(keyword);
            });
        }
    }
    private void search() {
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            showRecentSearchAndOrders();
        });

        // Sự kiện khi người dùng thay đổi trường tìm kiếm
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String query = etSearch.getText().toString().trim();

                if (TextUtils.isEmpty(query)) {
                    showRecentSearchAndOrders();
                    lvSearchResults.setVisibility(View.GONE);
                } else {
                    if (userId != -1)
                    {
                        saveSearch(query);
                    }
                    performSearch(query);
                }
                return true;
            }
            return false;
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSearch.setVisibility(s.length() == 0 ? View.VISIBLE : View.GONE);
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                if (s.length() == 0) {
                    showRecentSearchAndOrders();
                    lvSearchResults.setVisibility(View.GONE);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // Hàm thực hiện tìm kiếm và hiển thị kết quả
    private void performSearch(String query) {
        // Ẩn danh sách tìm kiếm và đơn hàng gần đây
        hideRecentSearchAndOrders();

        // Hiển thị kết quả tìm kiếm
        lvSearchResults.setVisibility(View.VISIBLE);

        // Thực hiện tìm kiếm sản phẩm (giả sử là tìm trong danh sách menuItems)
        searchResults = searchInMenuItems(query);

        // Cài đặt adapter cho RecyclerView kết quả tìm kiếm
        MenuItemAdapter menuItemAdapter = new MenuItemAdapter(this, searchResults, true);
        lvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        lvSearchResults.setAdapter(menuItemAdapter);
    }

    // Hàm tìm kiếm sản phẩm trong danh sách menuItems
    private List<MenuItem> searchInMenuItems(String query) {
        List<MenuItem> results = new ArrayList<>();
        // Giả sử bạn có danh sách menuItems, tìm kiếm các sản phẩm phù hợp với từ khóa
        for (MenuItem item : getMenuItems()) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                results.add(item);
            }
        }
        return results;
    }

    // Hàm lấy danh sách sản phẩm mẫu (hoặc có thể lấy từ API)
    private List<MenuItem> getMenuItems() {
        return allMenuItems;
    }
    private void fetchAllMenuItems() {
        apiService.getAllMenuItems().enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Call<List<MenuItem>> call, retrofit2.Response<List<MenuItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allMenuItems = response.body();
                } else {
                    Toast.makeText(SearchActivity.this, "Không thể lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm hiển thị lại danh sách tìm kiếm gần đây và đơn hàng gần đây khi không tìm kiếm


    private void showRecentSearchAndOrders() {
        btnClearSearch.setVisibility(View.GONE);
        lvSearchResults.setVisibility(View.GONE);
        if (userId != -1) {
            recentSearchesHeader.setVisibility(View.VISIBLE);
            tvheaderCategory.setVisibility(View.VISIBLE);
            lvRecentSearch.setVisibility(View.VISIBLE);
            frameLayoutRecentOrders.setVisibility(View.VISIBLE);
        }
    }

    private void hideRecentSearchAndOrders() {
        btnSearch.setVisibility(View.GONE);
        btnClearSearch.setVisibility(View.VISIBLE);
        lvSearchResults.setVisibility(View.GONE);
        recentSearchesHeader.setVisibility(View.GONE);
        tvheaderCategory.setVisibility(View.GONE);
        lvRecentSearch.setVisibility(View.GONE);
        frameLayoutRecentOrders.setVisibility(View.GONE);
    }

    //Xử lý bộ lọc
    public void applyFilters() {
        Button btnApplyFilter = findViewById(R.id.btnApplyFilter);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        RadioGroup rgPriceSort = findViewById(R.id.rgPriceSort);
        RadioGroup rgFavoriteSort = findViewById(R.id.rgFavoriteSort);

        btnApplyFilter.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            String selectedCategory = spinnerCategory.getSelectedItem().toString();

            // Lấy categoryId từ categoryList
            selectedCategoryId = -1; // Mặc định là "Tất cả"
            if (!selectedCategory.equals("All")) {
                for (Category category : categoryList) {
                    if (category.getType().equals(selectedCategory)) {
                        selectedCategoryId = category.getId(); // Lấy id của category
                        break;
                    }
                }
            }

            boolean sortByPriceAsc = rgPriceSort.getCheckedRadioButtonId() == R.id.rbPriceAsc;
            boolean sortByPriceDesc = rgPriceSort.getCheckedRadioButtonId() == R.id.rbPriceDesc;

            boolean sortByFavAsc = rgFavoriteSort.getCheckedRadioButtonId() == R.id.rbFavAsc;
            boolean sortByFavDesc = rgFavoriteSort.getCheckedRadioButtonId() == R.id.rbFavDesc;

            List<MenuItem> filteredList = new ArrayList<>(allMenuItems);

            // Lọc theo từ khóa
            if (!TextUtils.isEmpty(keyword)) {
                filteredList.removeIf(item -> !item.getName().toLowerCase().contains(keyword.toLowerCase()));
            }

            // Lọc theo categoryId nếu không phải "None"
            if (selectedCategoryId != -1) {
                filteredList.removeIf(item -> item.getCategoryId() != selectedCategoryId);
            }

            // Sắp xếp theo giá
            if (sortByPriceAsc) {
                filteredList.sort(Comparator.comparing(MenuItem::getPrice));
            } else if (sortByPriceDesc) {
                filteredList.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
            }

            // Sắp xếp theo độ yêu thích
            if (sortByFavAsc) {
                filteredList.sort(Comparator.comparing(MenuItem::getFavoriteCount));
            } else if (sortByFavDesc) {
                filteredList.sort((a, b) -> Integer.compare(b.getFavoriteCount(), a.getFavoriteCount()));
            }
            hideRecentSearchAndOrders();

            // Hiển thị kết quả tìm kiếm
            lvSearchResults.setVisibility(View.VISIBLE);
            // Hiển thị kết quả
            showSearchResults(filteredList);

            // Đóng drawer
            drawerLayout.closeDrawer(Gravity.LEFT);
        });
    }
    private void showSearchResults(List<MenuItem> results) {
        searchResults = results;
        MenuItemAdapter adapter = new MenuItemAdapter(this, searchResults, true);
        lvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        lvSearchResults.setAdapter(adapter);
        lvSearchResults.setVisibility(View.VISIBLE);
    }

    //Xử lý Spinner
    private void spinnerSetup() {
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        List<Category> categoryList = new ArrayList<>();
        List<String> categoryTypes = new ArrayList<>();

        // Thêm mục "Tất cả" vào đầu danh sách
        categoryTypes.add("All");

        apiService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("SearchActivity", "Categories fetched successfully: " + response.body().toString());
                    categoryList.addAll(response.body());

                    // Thêm các type của category vào spinner sau "Tất cả"
                    for (Category category : categoryList) {
                        categoryTypes.add(category.getType());
                    }

                    // Tạo adapter với "All" đầu tiên
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            SearchActivity.this,
                            android.R.layout.simple_spinner_item,
                            categoryTypes
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Failed to load Category", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Lấy danh sách tìm kiếm gần đây
    private void getRecentSearches() {
        RecentSearchRequest request = new RecentSearchRequest(userId);
        apiService.getRecentSearches(request).enqueue(new Callback<List<RecentSearch>>() {
            @Override
            public void onResponse(Call<List<RecentSearch>> call, Response<List<RecentSearch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recentList.clear();
                    recentList.addAll(response.body());
                    recentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<RecentSearch>> call, Throwable t) {
                Log.e("API", "Lỗi lấy recent search", t);
            }
        });
    }
    private void saveSearch(String keyword) {
        RecentSearchRequest request = new RecentSearchRequest(keyword, userId);
        apiService.saveSearch(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                getRecentSearches();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Lỗi lưu search", t);
            }
        });
    }

    private void clearAllRecentSearches() {
        apiService.clearAllSearches(new RecentSearchRequest(userId)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                recentList.clear();
                recentAdapter.notifyDataSetChanged();
                Toast.makeText(SearchActivity.this, "Đã xoá tất cả", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSearch(String keyword) {
        apiService.deleteSearch(new RecentSearchRequest(keyword, userId)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                getRecentSearches();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Lỗi xoá từ khoá", t);
            }
        });
    }

    private void fetchRecentOrders() {
        // Gọi API để lấy các đơn hàng gần đây
        apiService.getRecentOrderItems(userId).enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Cập nhật dữ liệu vào RecyclerView
                    recentOrders.clear();
                    recentOrders.addAll(response.body());
                    orderAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Failed to fetch recent orders: " + response.message() + " " + response.code() + " " + response.errorBody().toString() + " " + response.headers().toString() + " " + response.raw().toString() + " " + response.raw().request());
                    Toast.makeText(SearchActivity.this, "Cannot load recent orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                Log.e("API", "Error fetching recent orders");
                Toast.makeText(SearchActivity.this, "Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}