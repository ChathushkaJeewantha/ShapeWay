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


public class Q9 extends Fragment {
    SignUpViewModel viewModel;
    MaterialCardView q9card1, q9card2, q9card3, q9card4;
    RadioButton q9r1, q9r2, q9r3, q9r4;

    public Q9() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);
        q9card1 = view.findViewById(R.id.q9card1);
        q9card2 = view.findViewById(R.id.q9card2);
        q9card3 = view.findViewById(R.id.q9card3);
        q9card4 = view.findViewById(R.id.q9card4);


        q9r1 = view.findViewById(R.id.q9r1);
        q9r2 = view.findViewById(R.id.q9r2);
        q9r3 = view.findViewById(R.id.q9r3);
        q9r4 = view.findViewById(R.id.q9r4);


        q9card1.setOnClickListener(v -> select(q9r1, "9to5"));
        q9card2.setOnClickListener(v -> select(q9r2, "Night"));
        q9card3.setOnClickListener(v -> select(q9r3, "Flexible"));
        q9card4.setOnClickListener(v -> select(q9r4, "NotWorking"));


    }

    private void select(RadioButton r, String wkSchedule) {

        q9r1.setChecked(false);
        q9r2.setChecked(false);
        q9r3.setChecked(false);
        q9r4.setChecked(false);


        r.setChecked(true);
        viewModel.wkSchedule = wkSchedule;
        new Handler().postDelayed(() -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.qContainer, new Q10())
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
        return inflater.inflate(R.layout.fragment_q9, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityQuestion1 activity = (ActivityQuestion1) getActivity();
        if (activity != null) {

            activity.updateProgress(9);

        }

    }
}