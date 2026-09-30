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


public class Q8 extends Fragment {

    SignUpViewModel viewModel;
    MaterialCardView q8card1, q8card2, q8card3, q8card4, q8card5;
    RadioButton q8r1, q8r2, q8r3, q8r4, q8r5;


    public Q8() {

        super(R.layout.fragment_q8);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);
        q8card1 = view.findViewById(R.id.q8card1);
        q8card2 = view.findViewById(R.id.q8card2);
        q8card3 = view.findViewById(R.id.q8card3);
        q8card4 = view.findViewById(R.id.q8card4);
        q8card5 = view.findViewById(R.id.q8card5);


        q8r1 = view.findViewById(R.id.q8r1);
        q8r2 = view.findViewById(R.id.q8r2);
        q8r3 = view.findViewById(R.id.q8r3);
        q8r4 = view.findViewById(R.id.q8r4);
        q8r5 = view.findViewById(R.id.q8r5);


        q8card1.setOnClickListener(v -> select(q8r1,"Traditional"));
        q8card2.setOnClickListener(v -> select(q8r2,"Keto"));
        q8card3.setOnClickListener(v -> select(q8r3,"Paleo"));
        q8card4.setOnClickListener(v -> select(q8r4,"Vegetarian"));
        q8card5.setOnClickListener(v -> select(q8r5,"Vegan"));



    }

    private void select(RadioButton r,String diet) {

        q8r1.setChecked(false);
        q8r2.setChecked(false);
        q8r3.setChecked(false);
        q8r4.setChecked(false);
        q8r5.setChecked(false);

        r.setChecked(true);
        viewModel.diet= diet;

        new Handler().postDelayed(() -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.qContainer, new Q9())
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
        return inflater.inflate(R.layout.fragment_q8, container, false);
    }
    @Override
    public void onResume() {
        super.onResume();
        ActivityQuestion1 activity = (ActivityQuestion1) getActivity();
        if (activity!=null){

            activity.updateProgress(8);

        }

    }
}