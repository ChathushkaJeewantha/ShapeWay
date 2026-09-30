package lk.jiat.shapeway.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import lk.jiat.shapeway.databinding.FragmentWalkBinding;
import lk.jiat.shapeway.walk.WalkPrefs;

public class WalkFragment extends Fragment {

    private FragmentWalkBinding binding;
    private WalkPrefs walkPrefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWalkBinding.inflate(inflater, container, false);
        walkPrefs = new WalkPrefs(requireContext());

        setupClicks();
        loadData();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();

    }

    private void setupClicks() {
        binding.btnSetTarget.setOnClickListener(v -> showTargetDialog());

        binding.btnOpenMap.setOnClickListener(v -> {
            if (walkPrefs.getRemainingSteps() <= 0) {
                Toast.makeText(requireContext(), "Today's target already completed.", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(requireContext(), WalkingMapActivity.class));
        });
    }

    private void loadData() {
        walkPrefs.ensureTodayReset();

        int target = walkPrefs.getDailyTarget();
        int current = walkPrefs.getTodaySteps();
        int remaining = walkPrefs.getRemainingSteps();

        binding.tvTargetValue.setText(String.valueOf(target));
        binding.tvCurrentSteps.setText(String.valueOf(current));
        binding.tvRemainingSteps.setText(String.valueOf(remaining));

        binding.stepProgress.setMax(target);
        binding.stepProgress.setProgress(Math.min(current, target));

        int percent = target == 0 ? 0 : (current * 100 / target);
        binding.tvProgressPercent.setText(percent + "% completed");

        if (remaining <= 0) {
            binding.tvSessionHint.setText("Great job. You completed today's walking goal.");
        } else {
            binding.tvSessionHint.setText("You still need " + remaining + " steps today.");
        }
    }

    private void showTargetDialog() {
        final com.google.android.material.textfield.TextInputEditText input =
                new com.google.android.material.textfield.TextInputEditText(requireContext());
        input.setHint("Enter target steps");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(walkPrefs.getDailyTarget()));
        input.setPadding(40, 40, 40, 20);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Set Daily Step Target")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String text = input.getText() != null ? input.getText().toString().trim() : "";

                    if (text.isEmpty()) {
                        Toast.makeText(requireContext(), "Please enter target steps", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int target = Integer.parseInt(text);

                        if (target < 5000) {
                            Toast.makeText(requireContext(), "Minimum target is 5000 steps", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        walkPrefs.setDailyTarget(target);
                        loadData();

                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Invalid number", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}