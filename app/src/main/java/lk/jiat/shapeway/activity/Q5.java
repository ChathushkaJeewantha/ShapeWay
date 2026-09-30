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


public class Q5 extends Fragment {
    SignUpViewModel viewModel;
    MaterialCardView q5card1, q5card2, q5card3, q5card4, q5card5;
    RadioButton q5r1, q5r2, q5r3, q5r4, q5r5;

    public Q5() {

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);
        q5card1 = view.findViewById(R.id.q5card1);
        q5card2 = view.findViewById(R.id.q5card2);
        q5card3 = view.findViewById(R.id.q5card3);
        q5card4 = view.findViewById(R.id.q5card4);
        q5card5 = view.findViewById(R.id.q5card5);

        q5r1 = view.findViewById(R.id.q5r1);
        q5r2 = view.findViewById(R.id.q5r2);
        q5r3 = view.findViewById(R.id.q5r3);
        q5r4 = view.findViewById(R.id.q5r4);
        q5r5 = view.findViewById(R.id.q5r5);

        q5card1.setOnClickListener(v -> select(q5r1,"Less10"));
        q5card2.setOnClickListener(v -> select(q5r2,"10to20"));
        q5card3.setOnClickListener(v -> select(q5r3,"21to30"));
        q5card4.setOnClickListener(v -> select(q5r4,"Than30"));
        q5card5.setOnClickListener(v -> select(q5r5,"DontKnow"));


    }

    private void select(RadioButton r,String pushUp) {

        q5r1.setChecked(false);
        q5r2.setChecked(false);
        q5r3.setChecked(false);
        q5r4.setChecked(false);
        q5r5.setChecked(false);

        r.setChecked(true);
        viewModel.pushUp=pushUp;

        new Handler().postDelayed(() -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.qContainer, new Q6())
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
        return inflater.inflate(R.layout.fragment_q5, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityQuestion1 activity = (ActivityQuestion1) getActivity();

        if (activity!=null){
            activity.updateProgress(5);

        }

    }
}