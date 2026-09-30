package lk.jiat.shapeway.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.google.android.material.card.MaterialCardView;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.activity.viewmodels.SignUpViewModel;


public class Q1 extends Fragment {

    SignUpViewModel viewModel;
    MaterialCardView questionCard1, questionCard2, questionCard3, questionCard4;
    RadioButton radioSelect1, radioSelect2, radioSelect3, radioSelect4;

    public Q1() {
        super(R.layout.fragment_q1);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        questionCard1 = view.findViewById(R.id.questionCard1);
        radioSelect1 = view.findViewById(R.id.radioSelect1);
        questionCard2 = view.findViewById(R.id.questionCard2);
        radioSelect2 = view.findViewById(R.id.radioSelect2);
        questionCard3 = view.findViewById(R.id.questionCard3);
        radioSelect3 = view.findViewById(R.id.radioSelect3);
        questionCard4 = view.findViewById(R.id.questionCard4);
        radioSelect4 = view.findViewById(R.id.radioSelect4);

        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);


        questionCard1.setOnClickListener(v -> {
            radioSelect1.setChecked(false);
            radioSelect2.setChecked(false);
            radioSelect3.setChecked(false);
            radioSelect4.setChecked(false);
            // select radio button
            radioSelect1.setChecked(true);

            viewModel.goal ="FewMuscles";

            // small delay so user sees the selection
            new Handler().postDelayed(() -> {

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.qContainer, new Q2())
                        .addToBackStack(null)
                        .commit();

            }, 200);

        });
        questionCard2.setOnClickListener(v -> {
            radioSelect1.setChecked(false);
            radioSelect2.setChecked(false);
            radioSelect3.setChecked(false);
            radioSelect4.setChecked(false);
            // select radio button
            radioSelect2.setChecked(true);

            viewModel.goal = "Athletic";

            // small delay so user sees the selection
            new Handler().postDelayed(() -> {

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.qContainer, new Q2())
                        .addToBackStack(null)
                        .commit();

            }, 200);

        });
        questionCard3.setOnClickListener(v -> {
            radioSelect1.setChecked(false);
            radioSelect2.setChecked(false);
            radioSelect3.setChecked(false);
            radioSelect4.setChecked(false);
            // select radio button
            radioSelect3.setChecked(true);
            viewModel.goal = "Shredded";

            // small delay so user sees the selection
            new Handler().postDelayed(() -> {

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.qContainer, new Q2())
                        .addToBackStack(null)
                        .commit();

            }, 200);

        });
        questionCard4.setOnClickListener(v -> {
            radioSelect1.setChecked(false);
            radioSelect2.setChecked(false);
            radioSelect3.setChecked(false);
            radioSelect4.setChecked(false);
            // select radio button
            radioSelect4.setChecked(true);
            viewModel.goal = "Swole";

            // small delay so user sees the selection
            new Handler().postDelayed(() -> {

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.qContainer, new Q2())
                        .addToBackStack(null)
                        .commit();

            }, 200);

        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q1, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        ActivityQuestion1 activity = (ActivityQuestion1) getActivity();
        if (activity != null) {
            activity.updateProgress(1);
        }

    }
}