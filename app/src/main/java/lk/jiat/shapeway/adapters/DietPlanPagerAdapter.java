package lk.jiat.shapeway.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import lk.jiat.shapeway.activity.DietPlanFragment;
import lk.jiat.shapeway.activity.FavouritesFragment;

public class DietPlanPagerAdapter extends FragmentStateAdapter {

    private final String dietType;

    public DietPlanPagerAdapter(@NonNull Fragment fragment, String dietType) {
        super(fragment);
        this.dietType = dietType;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return DietPlanFragment.newInstance(dietType);
            case 1: return FavouritesFragment.newInstance();
            default: return DietPlanFragment.newInstance(dietType);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
