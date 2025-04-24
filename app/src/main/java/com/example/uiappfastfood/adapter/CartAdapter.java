package com.example.uiappfastfood.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.model.CartItem;
import java.util.List;
import com.example.uiappfastfood.api.RetrofitClient;
import com.example.uiappfastfood.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final Context context;
    private final List<CartItem> cartItems;
    private final OnCartUpdatedListener onCartUpdatedListener;
    private final OnCartEmptyListener onCartEmptyListener;

    public interface OnCartUpdatedListener {
        void onCartUpdated();
    }

    public interface OnCartEmptyListener {
        void onCartEmpty();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartUpdatedListener onCartUpdatedListener, OnCartEmptyListener onCartEmptyListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.onCartUpdatedListener = onCartUpdatedListener;
        this.onCartEmptyListener = onCartEmptyListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        Glide.with(context)
                .load(cartItem.getImg())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_edit_avatar)
                .into(holder.ivImg);

        holder.tvName.setText(cartItem.getName());
        holder.tvPrice.setText(String.format("%,.0fđ", cartItem.getPrice()));
        holder.tv_itemQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.cbCartItem.setChecked(cartItem.isChecked());

        // Xử lý sự kiện khi checkbox thay đổi
        holder.cbCartItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartItem.setChecked(isChecked);
            if (onCartUpdatedListener != null) {
                onCartUpdatedListener.onCartUpdated(); // Gọi callback để cập nhật Payment Summary
            }
        });

        // Xử lý tăng số lượng
        holder.ivPlus.setOnClickListener(v -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            holder.tv_itemQuantity.setText(String.valueOf(cartItem.getQuantity()));
            if (onCartUpdatedListener != null) {
                onCartUpdatedListener.onCartUpdated();
            }
        });

        // Xử lý giảm số lượng
        holder.ivMinus.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                holder.tv_itemQuantity.setText(String.valueOf(cartItem.getQuantity()));
                if (onCartUpdatedListener != null) {
                    onCartUpdatedListener.onCartUpdated();
                }
            }
        });

        // delete cart item
        holder.ivDelete.setOnClickListener(v -> {

            System.out.println("Clicked ");
            //api delete cart item//
            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            Long userId = 1L;
            Long itemId = cartItem.getItemId();
            apiService.deleteCartItem(userId, itemId).enqueue(new Callback<Boolean>(){
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful() && response.body() != null){
                        Boolean isDeleted = response.body();
                        System.out.println("Check xoa: "+ isDeleted);
                        if (isDeleted) {
                            Toast.makeText(context, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                            cartItems.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, cartItems.size());

                            if (onCartUpdatedListener != null) {
                                onCartUpdatedListener.onCartUpdated();
                            }

                            if (cartItems.isEmpty() && onCartEmptyListener != null) {
                                onCartEmptyListener.onCartEmpty();
                            }
                        } else {
                            Toast.makeText(context, "Xóa sản phẩm khỏi giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.e("CartActivity", "API Coupon Error: " + t.getMessage());
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImg, ivDelete, ivPlus, ivMinus;
        TextView tvName, tvPrice, tv_itemQuantity;
        AppCompatCheckBox cbCartItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.iv_cartItemImg);
            tvName = itemView.findViewById(R.id.tv_cartItemName);
            tvPrice = itemView.findViewById(R.id.tv_cartItemPrice);
            tv_itemQuantity = itemView.findViewById(R.id.tv_itemQuantity);
            cbCartItem = itemView.findViewById(R.id.cb_cartItem);
            ivPlus = itemView.findViewById(R.id.iv_plus);
            ivMinus = itemView.findViewById(R.id.iv_minus);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}
