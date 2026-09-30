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

import java.util.ArrayList;
import java.util.List;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.models.MealModel;
import lk.jiat.shapeway.models.MealSection;

public class MealSectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_MEAL   = 1;

    public interface OnMealClickListener {
        void onMealClick(MealModel meal);
    }

    private final List<Object>          items    = new ArrayList<>();
    private final OnMealClickListener   listener;

    public MealSectionAdapter(List<MealSection> sections, OnMealClickListener listener) {
        this.listener = listener;
        for (MealSection section : sections) {
            items.add(section.getSectionTitle());   // String  → header row
            items.addAll(section.getMeals());       // MealModel → meal row
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof String) ? VIEW_TYPE_HEADER : VIEW_TYPE_MEAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View v = inflater.inflate(R.layout.item_meal_section_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_meal_card, parent, false);
            return new MealViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((String) items.get(position));
        } else {
            ((MealViewHolder) holder).bind((MealModel) items.get(position), listener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ─────────────────────────── Header ViewHolder ───────────────────────────
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvSectionTitle;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSectionTitle = itemView.findViewById(R.id.tvSectionTitle);
        }

        void bind(String title) {
            tvSectionTitle.setText(title);
        }
    }

    // ─────────────────────────── Meal ViewHolder ─────────────────────────────
    static class MealViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardMeal;          // ← root card — receives the tap
        ImageView        ivMealThumbnail;
        TextView         tvMealName;
        TextView         tvMealCalories;
        TextView         tvMealTime;
        ImageView        ivFavouriteToggle;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            // Attach to the card's own id so nothing intercepts the touch
            cardMeal           = itemView.findViewById(R.id.cardMeal);
            ivMealThumbnail    = itemView.findViewById(R.id.ivMealThumbnail);
            tvMealName         = itemView.findViewById(R.id.tvMealName);
            tvMealCalories     = itemView.findViewById(R.id.tvMealCalories);
            tvMealTime         = itemView.findViewById(R.id.tvMealTime);
            ivFavouriteToggle  = itemView.findViewById(R.id.ivFavouriteToggle);
        }

        void bind(MealModel meal, OnMealClickListener listener) {

            tvMealName.setText(meal.getName());
            tvMealCalories.setText(meal.getCalories() + " kcal");
            tvMealTime.setText(meal.getCookingTime() + " mins");

            // Load image with Glide
            if (meal.getImageUrl() != null && !meal.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(meal.getImageUrl())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                        .placeholder(R.drawable.placeholder_meal)
                        .error(R.drawable.placeholder_meal)
                        .into(ivMealThumbnail);
            }

            // ── Card click → open recipe ──────────────────────────────────
            // Set on the MaterialCardView directly (not itemView wrapper)
            // so the card's ripple and touch handling work correctly.
            cardMeal.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClick(meal);
                }
            });

            // ── Favourite toggle — independent click, stops propagation ───
            if (ivFavouriteToggle != null) {
                ivFavouriteToggle.setOnClickListener(v -> {
                    // Prevent the card click from also firing
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    // TODO: wire up favourite toggle logic here if needed
                });
            }
        }
    }
}