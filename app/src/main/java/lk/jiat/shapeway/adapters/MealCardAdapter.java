package lk.jiat.shapeway.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.models.MealModel;

public class MealCardAdapter extends RecyclerView.Adapter<MealCardAdapter.MealViewHolder> {

    public interface OnMealClickListener {
        void onMealClick(MealModel meal);
    }

    private final List<MealModel>     meals;
    private final OnMealClickListener listener;

    public MealCardAdapter(List<MealModel> meals, OnMealClickListener listener) {
        this.meals    = meals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_card, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        holder.bind(meals.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardMeal;
        ImageView        ivMealThumbnail;
        TextView         tvMealName;
        TextView         tvMealCalories;
        TextView         tvMealTime;
        ImageView        ivFavouriteToggle;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMeal          = itemView.findViewById(R.id.cardMeal);
            ivMealThumbnail   = itemView.findViewById(R.id.ivMealThumbnail);
            tvMealName        = itemView.findViewById(R.id.tvMealName);
            tvMealCalories    = itemView.findViewById(R.id.tvMealCalories);
            tvMealTime        = itemView.findViewById(R.id.tvMealTime);
            ivFavouriteToggle = itemView.findViewById(R.id.ivFavouriteToggle);
        }

        void bind(MealModel meal, OnMealClickListener listener) {

            tvMealName.setText(meal.getName());
            tvMealCalories.setText(meal.getCalories() + " kcal");
            tvMealTime.setText(meal.getCookingTime() + " mins");

            if (meal.getImageUrl() != null && !meal.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(meal.getImageUrl())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                        .placeholder(R.drawable.placeholder_meal)
                        .error(R.drawable.placeholder_meal)
                        .into(ivMealThumbnail);
            }

            // ── Card click → open recipe ──────────────────────────────────
            cardMeal.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClick(meal);
                }
            });

            // ── Favourite toggle — independent click ──────────────────────
            if (ivFavouriteToggle != null) {
                ivFavouriteToggle.setOnClickListener(v -> {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    // TODO: wire up favourite toggle logic here if needed
                });
            }
        }
    }
}