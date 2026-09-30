package lk.jiat.shapeway.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.databinding.FragmentPlanBinding;
import lk.jiat.shapeway.models.PlanItem;
import lk.jiat.shapeway.adapters.PlanAdapter;



public class Plan extends Fragment {

    private FragmentPlanBinding binding;
    RecyclerView recyclerView;
    TextView txtDate;
    public Plan() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPlanBinding.inflate(inflater, container, false);



        // Date
        String currentDate = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
                .format(new Date());
        binding.txtDate.setText(currentDate);

        binding.planRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        List<PlanItem> list = new ArrayList<>();

        list.add(new PlanItem("Log Calories", "450 of 1500 kcal", R.drawable.food, 30));
        list.add(new PlanItem("Do Your Workout", "Full body", R.drawable.dumbbell, 0));
        list.add(new PlanItem("Update Measurements", "Regular check-in", R.drawable.scale, 0));
        list.add(new PlanItem("Water Tracker", "2000 of 2750 ml", R.drawable.water, 70));
        list.add(new PlanItem("Walk", "100 of 8000 steps", R.drawable.shoes, 10));
        list.add(new PlanItem("Supplements", "Regular check-in", R.drawable.ic_supplements, 0));


        binding.planRecycler.setAdapter(new PlanAdapter(getContext(), list));



        return binding.getRoot();




    }
}