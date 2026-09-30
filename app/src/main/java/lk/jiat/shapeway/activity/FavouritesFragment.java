package lk.jiat.shapeway.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.adapters.MealCardAdapter;
import lk.jiat.shapeway.models.MealModel;

public class FavouritesFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private MealCardAdapter adapter;
    private View emptyState;
    private View progressBar;

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.rvFavourites);
        emptyState = view.findViewById(R.id.emptyState);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFavourites();
    }

    private void loadFavourites() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        db.collection("users")
                .document(currentUser.getUid())
                .collection("favourites")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    List<String> mealIds = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        mealIds.add(doc.getId());
                    }

                    if (mealIds.isEmpty()) {
                        showEmptyState();
                        return;
                    }

                    fetchMealsByIds(mealIds);
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    showEmptyState();
                });
    }

    private void fetchMealsByIds(List<String> mealIds) {
        List<MealModel> meals = new ArrayList<>();
        final int[] count = {0};

        for (String id : mealIds) {
            db.collection("diets").document(id)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            MealModel meal = doc.toObject(MealModel.class);
                            if (meal != null) {
                                meal.setDocumentId(doc.getId());
                                meals.add(meal);
                            }
                        }
                        count[0]++;
                        if (count[0] == mealIds.size()) {
                            if (meals.isEmpty()) {
                                showEmptyState();
                            } else {
                                showMeals(meals);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        count[0]++;
                        if (count[0] == mealIds.size()) {
                            if (meals.isEmpty()) showEmptyState();
                            else showMeals(meals);
                        }
                    });
        }
    }

    private void showEmptyState() {
        if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showMeals(List<MealModel> meals) {
        if (emptyState != null) emptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter = new MealCardAdapter(meals, meal -> {
            Bundle bundle = new Bundle();
            bundle.putString("mealId", meal.getDocumentId());
            bundle.putString("mealName", meal.getName());
            bundle.putString("imageUrl", meal.getImageUrl());
            bundle.putInt("calories", meal.getCalories());
            bundle.putInt("protein", meal.getProtein());
            bundle.putInt("carbs", meal.getCarbs());
            bundle.putInt("fats", meal.getFats());
            bundle.putInt("cookingTime", meal.getCookingTime());
            bundle.putString("dietType", meal.getDietType());
            bundle.putString("mealType", meal.getMealType());
            bundle.putStringArrayList("tags", new ArrayList<>(meal.getTags()));
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_dietFragment_to_recipeFragment, bundle);
        });
        recyclerView.setAdapter(adapter);
    }
}