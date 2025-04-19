package com.example.uiappfastfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiappfastfood.R;
import com.example.uiappfastfood.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private List<Category> categoryList;
    private int selectedPosition = 0;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public CategoryAdapter(Context context, List<Category> categoryList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.onItemClickListener = onItemClickListener;
    }

    public void selectCategory(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(selectedPosition);
    }

    public void updateCategories(List<Category> newCategories) {
        this.categoryList = newCategories;
        selectedPosition = 0;
        notifyDataSetChanged();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;
        CardView categoryCard;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryCard = itemView.findViewById(R.id.category_card);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getType());

        Glide.with(context)
                .load(category.getImgCategory())
                .placeholder(R.drawable.test_image)
                .error(R.drawable.test_image)
                .into(holder.categoryImage);

        boolean isSelected = position == selectedPosition;

        holder.categoryCard.setCardBackgroundColor(ContextCompat.getColor(
                context, isSelected ? R.color.orange : android.R.color.white
        ));

        holder.categoryName.setTextColor(ContextCompat.getColor(
                context, isSelected ? android.R.color.white : android.R.color.darker_gray
        ));

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION || currentPosition == selectedPosition) return;

            selectCategory(currentPosition);

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(categoryList.get(currentPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }
}

