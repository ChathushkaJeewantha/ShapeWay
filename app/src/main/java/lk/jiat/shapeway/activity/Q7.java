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


public class Q7 extends Fragment {
    SignUpViewModel viewModel;
    MaterialCardView q7card1, q7card2, q7card3, q7card4;
    RadioButton q7r1, q7r2, q7r3, q7r4;


    public Q7() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);
        q7card1 = view.findViewById(R.id.q7card1);
        q7card2 = view.findViewById(R.id.q7card2);
        q7card3 = view.findViewById(R.id.q7card3);
        q7card4 = view.findViewById(R.id.q7card4);


        q7r1 = view.findViewById(R.id.q7r1);
        q7r2 = view.findViewById(R.id.q7r2);
        q7r3 = view.findViewById(R.id.q7r3);
        q7r4 = view.findViewById(R.id.q7r4);


        q7card1.setOnClickListener(v -> select(q7r1,"Sitting"));
        q7card2.setOnClickListener(v -> select(q7r2,"Feet"));
        q7card3.setOnClickListener(v -> select(q7r3,"Balance"));
        q7card4.setOnClickListener(v -> select(q7r4,"Change"));



    }

    private void select(RadioButton r,String dailyRt){

        q7r1.setChecked(false);
        q7r2.setChecked(false);
        q7r3.setChecked(false);
        q7r4.setChecked(false);

        r.setChecked(true);
        viewModel.dailyRt=dailyRt;

        new Handler().postDelayed(() -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.qContainer, new Q8())
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
        return inflater.inflate(R.layout.fragment_q7, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityQuestion1 activity = (ActivityQuestion1) getActivity();
        if (activity!=null){

            activity.updateProgress(7);

        }

    }
}