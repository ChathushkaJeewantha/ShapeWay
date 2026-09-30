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


public class Q4 extends Fragment {
    SignUpViewModel viewModel;
    MaterialCardView q4card1, q4card2, q4card3, q4card4;
    RadioButton q4r1, q4r2, q4r3, q4r4;


    public Q4() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);

        q4card1 = view.findViewById(R.id.q4card1);
        q4card2 = view.findViewById(R.id.q4card2);
        q4card3 = view.findViewById(R.id.q4card3);
        q4card4 = view.findViewById(R.id.q4card4);

        q4r1 = view.findViewById(R.id.q4radio1);
        q4r2 = view.findViewById(R.id.q4radio2);
        q4r3 = view.findViewById(R.id.q4radio3);
        q4r4 = view.findViewById(R.id.q4radio4);

        q4card1.setOnClickListener(v -> select(q4r1, "Slender"));
        q4card2.setOnClickListener(v -> select(q4r2, "MediumBuild"));
        q4card3.setOnClickListener(v -> select(q4r3, "Stocky"));
        q4card4.setOnClickListener(v -> select(q4r4, "OverWeight"));
    }

    private void select(RadioButton r, String cb) {

        q4r1.setChecked(false);
        q4r2.setChecked(false);
        q4r3.setChecked(false);
        q4r4.setChecked(false);

        r.setChecked(true);
        viewModel.currentBody = cb;

        new Handler().postDelayed(() -> {

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.qContainer, new Q5())
                    .addToBackStack(null)
                    .commit();

        }, 200);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q4, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityQuestion1 activity = (ActivityQuestion1) getActivity();
        if (activity != null) {

            activity.updateProgress(4);

        }

    }
}