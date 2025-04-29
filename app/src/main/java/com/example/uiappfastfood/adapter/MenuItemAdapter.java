package com.example.uiappfastfood.adapter;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.activity.MenuActivity;
import com.example.uiappfastfood.api.ApiService;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.model.MenuItem;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {

    private Context context;
    private List<MenuItem> menuItemList;
    private boolean showFavorite;


    // Constructor với tham số showFavorite
    public MenuItemAdapter(Context context, List<MenuItem> menuItemList, boolean showFavorite) {
        this.context = context;
        this.menuItemList = menuItemList;
        this.showFavorite = showFavorite;  // Khởi tạo cờ điều kiện
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem menuItem = menuItemList.get(position);

        // Gán dữ liệu vào các view
        holder.nameTextView.setText(menuItem.getName());
        holder.priceTextView.setText(String.format("%,.0fđ", menuItem.getPrice()));

        // Load ảnh sản phẩm bằng Glide
        Glide.with(context).load(menuItem.getImgMenuItem()).into(holder.productImageView);

        // Kiểm tra và ẩn icon yêu thích nếu showFavorite là false
        if (!showFavorite) {
            holder.favoriteIcon.setVisibility(View.GONE);  // Ẩn icon yêu thích
        } else {
            holder.favoriteIcon.setVisibility(View.VISIBLE);  // Hiển thị icon yêu thích
            // Lấy userId từ SharedPrefManager
            SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
            long userId = sharedPrefManager.getUserId();

            // Cập nhật icon yêu thích nếu có
            updateFavoriteIcon(holder, menuItem, userId);

            // Xử lý sự kiện click vào icon yêu thích
            holder.favoriteIcon.setOnClickListener(v -> toggleFavorite(menuItem, holder));
        }

        // Sự kiện click vào sản phẩm
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MenuActivity.class);
            intent.putExtra("menu_item_id", menuItem.getId());
            intent.putExtra("menu_item_name", menuItem.getName());
            intent.putExtra("menu_item_price", menuItem.getPrice());
            intent.putExtra("menu_item_desc", menuItem.getDescription());
            intent.putExtra("menu_item_img", menuItem.getImgMenuItem());
            intent.putExtra("menu_item_category", menuItem.getCategoryId());
            List<Long> favoriteIds = menuItem.getUserFavoriteIds();
            long[] favoriteIdArray = new long[favoriteIds.size()];
            for (int i = 0; i < favoriteIds.size(); i++) {
                favoriteIdArray[i] = favoriteIds.get(i);
            }

            intent.putExtra("menu_item_favorite_ids", favoriteIdArray);

            // Kiểm tra giá trị của contextClassName và chỉ thay đổi nếu không phải là "MainActivity", "SearchActivity" hoặc "GuestActivity"
            String contextClassName = context.getClass().getSimpleName();
            SharedPrefManager sharedPrefManager = new SharedPrefManager(this.context);

            if (contextClassName.equals("MainActivity") ||
                    contextClassName.equals("SearchActivity") ||
                    contextClassName.equals("GuestActivity")) {
                sharedPrefManager.saveContext(contextClassName);
                intent.putExtra("context_class_simple", contextClassName);
            }
            else
            {
                String ContextName = sharedPrefManager.getShareContext();
                intent.putExtra("context_class_simple", ContextName);
            }
            Log.e("contextClassName", "contextClassName: " + sharedPrefManager.getShareContext());
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        });
    }


    @Override
    public int getItemCount() {
        return menuItemList.size();
    }

    // Phương thức cập nhật danh sách sản phẩm
    public void updateMenuItems(List<MenuItem> newMenuItems) {
        this.menuItemList = newMenuItems;
        notifyDataSetChanged();
    }

    // Phương thức gọi API để thêm hoặc xóa sản phẩm yêu thích
    private void toggleFavorite(MenuItem menuItem, ViewHolder holder) {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
        long userId = sharedPrefManager.getUserId();

        if (userId == -1) {
            // Nếu chưa đăng nhập, yêu cầu người dùng đăng nhập
            Toast.makeText(context, "Please log in to add to favorites", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu người dùng đã đăng nhập, tiếp tục thao tác yêu thích
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        // Kiểm tra nếu sản phẩm đã có trong danh sách yêu thích
        if (menuItem.getUserFavoriteIds() != null && menuItem.getUserFavoriteIds().contains(userId)) {
            // Xóa sản phẩm khỏi yêu thích
            removeFavorite(menuItem, apiService, userId, holder);
        } else {
            // Thêm sản phẩm vào yêu thích
            addFavorite(menuItem, apiService, userId, holder);
        }
    }

    // Thêm sản phẩm vào yêu thích
    private void addFavorite(MenuItem menuItem, ApiService apiService, long userId, ViewHolder holder) {
        apiService.addFavoriteItem(userId, menuItem.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Cập nhật UI sau khi thêm sản phẩm vào danh sách yêu thích
                    menuItem.getUserFavoriteIds().add(userId);
                    updateFavoriteIcon(holder, menuItem, userId);  // Cập nhật lại icon yêu thích
                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Failed to add favorite", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xóa sản phẩm khỏi yêu thích
    private void removeFavorite(MenuItem menuItem, ApiService apiService, long userId, ViewHolder holder) {
        apiService.removeFavoriteItem(userId, menuItem.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Cập nhật UI sau khi xóa sản phẩm khỏi danh sách yêu thích
                    menuItem.getUserFavoriteIds().remove(userId);
                    updateFavoriteIcon(holder, menuItem, userId);  // Cập nhật lại icon yêu thích
                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Failed to remove favorite", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức cập nhật icon yêu thích
    private void updateFavoriteIcon(ViewHolder holder, MenuItem menuItem, long userId) {
        if (userId != -1) {
            // Kiểm tra nếu userId có trong danh sách yêu thích
            boolean isFavorite = menuItem.getUserFavoriteIds().contains(userId);
            int favoriteIconRes = isFavorite ? R.drawable.ic_favorite_fill : R.drawable.ic_favorite_border;
            holder.favoriteIcon.setImageResource(favoriteIconRes);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView, favoriteIcon;
        TextView nameTextView, priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImage);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
        }
    }
}