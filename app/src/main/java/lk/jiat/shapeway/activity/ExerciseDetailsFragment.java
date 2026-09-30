package lk.jiat.shapeway.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.models.WorkoutPlan;


public class ExerciseDetailsFragment extends Fragment {

    private ImageView ivHero, ivBack;
    private TextView tvTitle, tvCalories, tvDuration, tvFocusZone, tvHealthCount;
    private LinearLayout llHealthNotice, llFocusZones;
    private MaterialButton btnStartWorkout;

    private FirebaseFirestore db;
    private WorkoutPlan plan;
    private String            workoutId;

    public ExerciseDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (FirebaseApp.getApps(requireContext()).isEmpty()) {
            FirebaseApp.initializeApp(requireContext());
        }
        db = FirebaseFirestore.getInstance();

        // Retrieve workoutId passed from WorkoutFragment
        if (getArguments() != null) {
            workoutId = getArguments().getString("workoutId", "");
        }

        bindViews(view);
        setupListeners();
        loadPlan();
    }

    // ── Bind Views ────────────────────────────────────────────────────────
    private void bindViews(View v) {
        ivHero         = v.findViewById(R.id.ivHero);
        ivBack         = v.findViewById(R.id.ivBack);
        tvTitle        = v.findViewById(R.id.tvTitle);
        tvCalories     = v.findViewById(R.id.tvCalories);
        tvDuration     = v.findViewById(R.id.tvDuration);
        tvFocusZone    = v.findViewById(R.id.tvFocusZone);
        tvHealthCount  = v.findViewById(R.id.tvHealthCount);
        llHealthNotice = v.findViewById(R.id.llHealthNotice);
        llFocusZones   = v.findViewById(R.id.llFocusZones);
        btnStartWorkout= v.findViewById(R.id.btnStartWorkout);
    }

    // ── Listeners ─────────────────────────────────────────────────────────
    private void setupListeners() {
        ivBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Health Notice popup
        llHealthNotice.setOnClickListener(v -> showHealthNoticeDialog());

        // Focus Zones bottom sheet
        llFocusZones.setOnClickListener(v -> showFocusZonesSheet());

        // Start Workout → WorkoutPlayerActivity
        btnStartWorkout.setOnClickListener(v -> {
            if (plan == null || plan.getExercises() == null || plan.getExercises().isEmpty()) return;
            Intent intent = new Intent(getContext(), WorkoutPlayerActivity.class);
            intent.putExtra(WorkoutPlayerActivity.EXTRA_WORKOUT_ID, workoutId);
            startActivity(intent);
        });
    }

    // ── Load Firestore ─────────────────────────────────────────────────────
    private void loadPlan() {
        if (workoutId == null || workoutId.isEmpty()) return;
        db.collection("workouts").document(workoutId)
                .get()
                .addOnSuccessListener(doc -> {
                    plan = doc.toObject(WorkoutPlan.class);
                    if (plan != null) {
                        plan.setId(doc.getId());
                        populateUI();
                    }
                });
    }

    // ── Populate UI ───────────────────────────────────────────────────────
    private void populateUI() {
        if (getContext() == null || plan == null) return;

        tvTitle.setText(plan.getTitle());
        tvCalories.setText(plan.getCalories() + " Calories");
        tvDuration.setText(plan.getDurationMinutes() + " Minutes");
        tvFocusZone.setText(plan.getFocusZone() != null ? plan.getFocusZone() : "—");

        if (plan.getThumbnailUrl() != null) {
            Glide.with(this)
                    .load(plan.getThumbnailUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_workout_placeholder)
                    .into(ivHero);
        }
    }

    // ── Health Notice AlertDialog ──────────────────────────────────────────
    private void showHealthNoticeDialog() {
        String notice = (plan != null && plan.getHealthNotice() != null)
                ? plan.getHealthNotice()
                : "Consult your doctor before starting if you have any pre-existing conditions.";

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Health Notice")
                .setMessage(notice)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Got it", null)
                .show();
    }

    // ── Focus Zones BottomSheet ────────────────────────────────────────────
    private void showFocusZonesSheet() {
        if (plan == null) return;

        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_focus_zones, null);

        TextView tvZone = sheetView.findViewById(R.id.tvZoneDetail);
        tvZone.setText(plan.getFocusZone() != null
                ? "Primary focus: " + plan.getFocusZone()
                : "Full body workout");

        // Add exercise categories list
        if (plan.getExercises() != null) {
            LinearLayout llList = sheetView.findViewById(R.id.llExerciseList);
            for (lk.jiat.shapeway.models.Exercise ex : plan.getExercises()) {
                TextView tv = new TextView(getContext());
                tv.setText("• " + ex.getTitle() + "  (" + ex.getDurationSeconds() + "s)");
                tv.setTextSize(13f);
                tv.setPadding(0, 8, 0, 8);
                llList.addView(tv);
            }
        }

        sheetView.findViewById(R.id.btnCloseSheet)
                .setOnClickListener(v -> sheet.dismiss());

        sheet.setContentView(sheetView);
        sheet.show();
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}