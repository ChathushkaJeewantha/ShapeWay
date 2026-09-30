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


public class Q2 extends Fragment {

    SignUpViewModel viewModel;

    MaterialCardView ectoCard, mesoCard, endoCard;
    RadioButton ectoRadio, mesoRadio, endoRadio;


    public Q2() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ectoCard = view.findViewById(R.id.ectoCard);
        ectoRadio = view.findViewById(R.id.ectoRadio);
        endoCard = view.findViewById(R.id.endoCard);
        endoRadio = view.findViewById(R.id.endoRadio);
        mesoCard = view.findViewById(R.id.mesoCard);
        mesoRadio = view.findViewById(R.id.mesoRadio);

        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);

        ectoCard.setOnClickListener(v -> {
            ectoRadio.setChecked(false);
            endoRadio.setChecked(false);
            mesoRadio.setChecked(false);
            // select radio button
            ectoRadio.setChecked(true);

            viewModel.somatotypes="Ectomorph";

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
                        .replace(R.id.qContainer, new Q3())
                        .addToBackStack(null)
                        .commit();

            }, 200);

        });

        endoCard.setOnClickListener(v -> {

            ectoRadio.setChecked(false);
            endoRadio.setChecked(false);
            mesoRadio.setChecked(false);
            // select radio button
            endoRadio.setChecked(true);

            viewModel.somatotypes="Endomorph";
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
                        .replace(R.id.qContainer, new Q3())
                        .addToBackStack(null)
                        .commit();

            }, 200);

        });
        mesoCard.setOnClickListener(v -> {
            ectoRadio.setChecked(false);
            endoRadio.setChecked(false);
            mesoRadio.setChecked(false);
            // select radio button
            mesoRadio.setChecked(true);

            viewModel.somatotypes="mesomorph";
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
                        .replace(R.id.qContainer, new Q3())
                        .addToBackStack(null)
                        .commit();

            }, 200);

        });


    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityQuestion1 activity = (ActivityQuestion1) getActivity();
        if (activity != null) {
            activity.updateProgress(2);
        }

    }
}