package lk.jiat.shapeway.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.adapters.MealSectionAdapter;
import lk.jiat.shapeway.models.MealModel;
import lk.jiat.shapeway.models.MealSection;

public class DietPlanFragment extends Fragment {

    private static final String ARG_DIET_TYPE = "diet_type";
    private String dietType;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private MealSectionAdapter adapter;
    private View emptyState;
    private View progressBar;

    private final List<String> mealTypes = Arrays.asList("Breakfast", "Lunch", "Dinner", "Snack");

    public static DietPlanFragment newInstance(String dietType) {
        DietPlanFragment fragment = new DietPlanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DIET_TYPE, dietType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dietType = getArguments().getString(ARG_DIET_TYPE, "Traditional");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diet_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvMealSections);
        emptyState = view.findViewById(R.id.emptyState);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadMeals();
    }

    private void loadMeals() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        db.collection("diets")
                .whereEqualTo("dietType", dietType)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    List<MealSection> sections = new ArrayList<>();

                    for (String mealType : mealTypes) {
                        List<MealModel> meals = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String type = doc.getString("mealType");
                            if (mealType.equalsIgnoreCase(type)) {
                                MealModel meal = doc.toObject(MealModel.class);
                                meal.setDocumentId(doc.getId());
                                meals.add(meal);
                            }
                        }
                        if (!meals.isEmpty()) {
                            sections.add(new MealSection(mealType, meals));
                        }
                    }

                    if (sections.isEmpty()) {
                        showEmptyState();
                    } else {
                        showMeals(sections);
                    }
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
                });
    }
    private void showEmptyState() {
        if (emptyState  != null) emptyState.setVisibility(View.VISIBLE);
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
    }

    private void showMeals(List<MealSection> sections) {
        if (emptyState  != null) emptyState.setVisibility(View.GONE);
        if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);

        adapter = new MealSectionAdapter(sections, this::navigateToRecipe);
        recyclerView.setAdapter(adapter);
    }

    private void navigateToRecipe(MealModel meal) {

        Bundle bundle = new Bundle();
        bundle.putString("mealId",     meal.getDocumentId());
        bundle.putString("mealName",   meal.getName());
        bundle.putString("imageUrl",   meal.getImageUrl());
        bundle.putInt("calories",      meal.getCalories());
        bundle.putInt("protein",       meal.getProtein());
        bundle.putInt("carbs",         meal.getCarbs());
        bundle.putInt("fats",          meal.getFats());
        bundle.putInt("cookingTime",   meal.getCookingTime());
        bundle.putString("dietType",   meal.getDietType());
        bundle.putString("mealType",   meal.getMealType());
        bundle.putStringArrayList("tags", new ArrayList<>(meal.getTags()));

        // ── Strategy 1: walk up to DietFragment (2 levels) ──────────────
        try {
            Fragment level1 = getParentFragment();                        // PagerAdapter host
            Fragment level2 = (level1 != null)
                    ? level1.getParentFragment() : null;                  // DietFragment

            if (level2 != null && level2.getView() != null) {
                NavController nc = NavHostFragment.findNavController(level2);
                nc.navigate(R.id.action_dietFragment_to_recipeFragment, bundle);
                return; // ✓ done
            }
        } catch (Exception e1) {
            // try next strategy
        }

        // ── Strategy 2: use fragment_container ID (your HomeActivity) ───
        try {
            NavController nc = Navigation.findNavController(
                    requireActivity(), R.id.fragment_container);
            nc.navigate(R.id.action_dietFragment_to_recipeFragment, bundle);
            return; // ✓ done
        } catch (Exception e2) {
            // try next strategy
        }

        // ── Strategy 3: scan fragment manager for NavHostFragment ────────
        try {
            List<Fragment> fragments = requireActivity()
                    .getSupportFragmentManager()
                    .getFragments();

            for (Fragment f : fragments) {
                if (f instanceof NavHostFragment) {
                    NavController nc = ((NavHostFragment) f).getNavController();
                    nc.navigate(R.id.action_dietFragment_to_recipeFragment, bundle);
                    return; // ✓ done
                }
                // one more level deep (e.g. bottom nav host wraps another host)
                if (f != null) {
                    List<Fragment> children = f.getChildFragmentManager().getFragments();
                    for (Fragment child : children) {
                        if (child instanceof NavHostFragment) {
                            NavController nc = ((NavHostFragment) child).getNavController();
                            nc.navigate(R.id.action_dietFragment_to_recipeFragment, bundle);
                            return; // ✓ done
                        }
                    }
                }
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

}