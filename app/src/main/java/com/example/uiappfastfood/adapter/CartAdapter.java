package com.example.uiappfastfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.ivImg.setImageResource(cartItem.getImageResId());
        holder.tvName.setText(cartItem.getName());
        holder.tvPrice.setText(cartItem.getPrice());
        holder.cbCartItem.setChecked(cartItem.isChecked());

        // Xử lý sự kiện khi checkbox thay đổi
        holder.cbCartItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartItem.setChecked(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImg;
        TextView tvName, tvPrice;
        AppCompatCheckBox cbCartItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.iv_cartItemImg);
            tvName = itemView.findViewById(R.id.tv_cartItemName);
            tvPrice = itemView.findViewById(R.id.tv_cartItemPrice);
            cbCartItem = itemView.findViewById(R.id.cb_cartItem);
        }
    }
}
