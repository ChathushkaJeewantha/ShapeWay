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


public class Q3 extends Fragment {
SignUpViewModel viewModel;
    MaterialCardView q3card1, q3card2, q3card3;
    RadioButton q3radio1,q3radio2, q3radio3;

    public Q3() {
        super(R.layout.fragment_q3);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        q3card1 = view.findViewById(R.id.q3Card1);
        q3card2 = view.findViewById(R.id.q3card2);
        q3card3 = view.findViewById(R.id.q3card3);

        q3radio1 = view.findViewById(R.id.q3radio1);
        q3radio2 = view.findViewById(R.id.q3radio2);
        q3radio3 = view.findViewById(R.id.q3radio3);
        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);

        q3card1.setOnClickListener(v -> selectOption(q3radio1,"GainHard"));
        q3card2.setOnClickListener(v -> selectOption(q3radio2, "GainMedium"));
        q3card3.setOnClickListener(v -> selectOption(q3radio3,"GainEasy"));

    }

    private void selectOption(RadioButton radio, String g){

        q3radio1.setChecked(false);
        q3radio2.setChecked(false);
        q3radio3.setChecked(false);

        radio.setChecked(true);
        viewModel.gain=g;

        new Handler().postDelayed(() -> {

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.qContainer, new Q4())
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
        return inflater.inflate(R.layout.fragment_q3, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityQuestion1 activity = (ActivityQuestion1) getActivity();
        if(activity != null){
            activity.updateProgress(3);
        }

    }
}