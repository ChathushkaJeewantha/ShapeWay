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


public class Q6 extends Fragment {
    SignUpViewModel viewModel;
    MaterialCardView q6card1, q6card2, q6card3;
    RadioButton q6r1, q6r2, q6r3;

    public Q6() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);
        q6card1 = view.findViewById(R.id.q6card1);
        q6card2 = view.findViewById(R.id.q6card2);
        q6card3 = view.findViewById(R.id.q6card3);


        q6r1 = view.findViewById(R.id.q6r1);
        q6r2 = view.findViewById(R.id.q6r2);
        q6r3 = view.findViewById(R.id.q6r3);


        q6card1.setOnClickListener(v -> select(q6r1, "1to2"));
        q6card2.setOnClickListener(v -> select(q6r2, "3to4"));
        q6card3.setOnClickListener(v -> select(q6r3, "5plus"));


    }

    private void select(RadioButton r, String tpw) {

        q6r1.setChecked(false);
        q6r2.setChecked(false);
        q6r3.setChecked(false);

        r.setChecked(true);

        viewModel.tpw = tpw;
        new Handler().postDelayed(() -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.qContainer, new Q7())
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
        return inflater.inflate(R.layout.fragment_q6, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityQuestion1 activity = (ActivityQuestion1) getActivity();
        if (activity != null) {

            activity.updateProgress(6);

        }

    }
}