package lk.jiat.shapeway.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lk.jiat.shapeway.R;

public class RecipeFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ImageView ivMealImage;
    private ImageView ivBack;
    private ImageView ivFavourite;
    private TextView tvMealName;
    private TextView tvCalories;
    private TextView tvProtein;
    private TextView tvCarbs;
    private TextView tvFats;
    private TextView tvCookingTime;
    private ChipGroup chipGroup;
    private ExtendedFloatingActionButton btnLogMeal;

    private String mealId;
    private String mealName;
    private String imageUrl;
    private int calories, protein, carbs, fats, cookingTime;
    private String dietType, mealType;
    private ArrayList<String> tags;

    private boolean isFavourited = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initViews(view);
        extractArgs();
        populateUI();
        checkIfFavourited();
        setupListeners(view);
    }

    private void initViews(View view) {
        ivMealImage = view.findViewById(R.id.ivMealImage);
        ivBack = view.findViewById(R.id.ivBack);
        ivFavourite = view.findViewById(R.id.ivFavourite);
        tvMealName = view.findViewById(R.id.tvMealName);
        tvCalories = view.findViewById(R.id.tvCalories);
        tvProtein = view.findViewById(R.id.tvProtein);
        tvCarbs = view.findViewById(R.id.tvCarbs);
        tvFats = view.findViewById(R.id.tvFats);
        tvCookingTime = view.findViewById(R.id.tvCookingTime);
        chipGroup = view.findViewById(R.id.chipGroup);
        btnLogMeal = view.findViewById(R.id.btnLogMeal);
    }

    private void extractArgs() {
        Bundle args = getArguments();
        if (args != null) {
            mealId = args.getString("mealId", "");
            mealName = args.getString("mealName", "");
            imageUrl = args.getString("imageUrl", "");
            calories = args.getInt("calories", 0);
            protein = args.getInt("protein", 0);
            carbs = args.getInt("carbs", 0);
            fats = args.getInt("fats", 0);
            cookingTime = args.getInt("cookingTime", 0);
            dietType = args.getString("dietType", "");
            mealType = args.getString("mealType", "");
            tags = args.getStringArrayList("tags");
            if (tags == null) tags = new ArrayList<>();
        }
    }

    private void populateUI() {
        tvMealName.setText(mealName);
        tvCalories.setText(String.valueOf(calories));
        tvProtein.setText(protein + "g");
        tvCarbs.setText(carbs + "g");
        tvFats.setText(fats + "g");
        tvCookingTime.setText(cookingTime + " min");

        if (!imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(ivMealImage);
        }

        // Add tags as chips
        chipGroup.removeAllViews();
        for (String tag : tags) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag);
            chip.setClickable(false);
            chip.setCheckable(false);
            chipGroup.addView(chip);
        }

        // Diet type chip
        if (!dietType.isEmpty()) {
            Chip dietChip = new Chip(requireContext());
            dietChip.setText(dietType.toLowerCase().replace("traditional", "omnivore"));
            dietChip.setClickable(false);
            dietChip.setCheckable(false);
            chipGroup.addView(dietChip, 0);
        }
    }

    private void checkIfFavourited() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || mealId.isEmpty()) return;

        db.collection("users")
                .document(currentUser.getUid())
                .collection("favourites")
                .document(mealId)
                .get()
                .addOnSuccessListener(doc -> {
                    isFavourited = doc.exists();
                    updateFavouriteIcon();
                });
    }

    private void updateFavouriteIcon() {
        if (ivFavourite == null) return;
        ivFavourite.setImageResource(isFavourited
                ? R.drawable.ic_heart_filled
                : R.drawable.ic_heart_outline);
    }

    private void setupListeners(View view) {
        ivBack.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        ivFavourite.setOnClickListener(v -> toggleFavourite());

        btnLogMeal.setOnClickListener(v -> logMeal());
    }

    private void toggleFavourite() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        if (isFavourited) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("favourites")
                    .document(mealId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        isFavourited = false;
                        updateFavouriteIcon();
                        Toast.makeText(getContext(), "Removed from favourites", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("addedAt", System.currentTimeMillis());
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("favourites")
                    .document(mealId)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(unused -> {
                        isFavourited = true;
                        updateFavouriteIcon();
                        Toast.makeText(getContext(), "Added to favourites", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void logMeal() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        Map<String, Object> log = new HashMap<>();
        log.put("mealId", mealId);
        log.put("mealName", mealName);
        log.put("calories", calories);
        log.put("protein", protein);
        log.put("carbs", carbs);
        log.put("fats", fats);
        log.put("mealType", mealType);
        log.put("dietType", dietType);
        log.put("loggedAt", System.currentTimeMillis());

        db.collection("users")
                .document(currentUser.getUid())
                .collection("mealLogs")
                .add(log)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(getContext(), "Meal logged successfully!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to log meal", Toast.LENGTH_SHORT).show());
    }

}