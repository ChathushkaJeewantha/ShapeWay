package lk.jiat.shapeway.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.adapters.DietPlanPagerAdapter;


public class DietFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userDietType = "Traditional"; // default

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextView tvGreeting;
    private TextView tvDietBadge;
    private View shimmerLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvDietBadge = view.findViewById(R.id.tvDietBadge);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);

        loadUserDietType();
    }

    private void loadUserDietType() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        shimmerLayout.setVisibility(View.VISIBLE);

        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String diet = documentSnapshot.getString("diet");
                        if (diet != null && !diet.isEmpty()) {
                            userDietType = diet;
                        }
                        String name = documentSnapshot.getString("name");
                        if (name != null && tvGreeting != null) {
                            tvGreeting.setText("Hello, " + name + " 👋");
                        }
                    }
                    shimmerLayout.setVisibility(View.GONE);
                    setupViewPager();
                    updateDietBadge();
                })
                .addOnFailureListener(e -> {
                    shimmerLayout.setVisibility(View.GONE);
                    setupViewPager();
                    updateDietBadge();
                });
    }

    private void updateDietBadge() {
        if (tvDietBadge != null) {
            tvDietBadge.setText(userDietType);
        }
    }

    private void setupViewPager() {
        DietPlanPagerAdapter adapter = new DietPlanPagerAdapter(this, userDietType);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Plan"); break;
                case 1: tab.setText("Favourites"); break;
            }
        }).attach();
    }
}