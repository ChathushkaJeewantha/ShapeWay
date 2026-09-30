package lk.jiat.shapeway.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.adapters.WorkoutAdapter;
import lk.jiat.shapeway.models.Workout;

public class WorkoutsFragment extends Fragment {

    private RecyclerView recyclerView;

    private WorkoutAdapter adapter;

    private List<Workout> workoutList;

    private FirebaseFirestore db;

    private static final String TAG = "WORKOUT_DEBUG";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_workouts,
                container,
                false);

        recyclerView = view.findViewById(R.id.recyclerWorkouts);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));

        workoutList = new ArrayList<>();

        adapter = new WorkoutAdapter(getContext(), workoutList);

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadWorkouts();

        return view;
    }

    private void loadWorkouts() {

        db.collection("workouts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    workoutList.clear();

                    Log.d(TAG, "Documents Count: "
                            + queryDocumentSnapshots.size());

                    for (QueryDocumentSnapshot document
                            : queryDocumentSnapshots) {

                        Workout workout =
                                document.toObject(Workout.class);

                        workoutList.add(workout);

                        Log.d(TAG,
                                "Workout Loaded: "
                                        + workout.getTitle());
                    }

                    adapter.notifyDataSetChanged();

                    Log.d(TAG,
                            "Adapter Updated");
                })

                .addOnFailureListener(e -> {

                    Log.e(TAG,
                            "Firestore Error: "
                                    + e.getMessage());
                });
    }
}