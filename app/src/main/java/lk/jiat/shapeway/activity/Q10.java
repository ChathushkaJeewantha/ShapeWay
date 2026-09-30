package lk.jiat.shapeway.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.activity.viewmodels.SignUpViewModel;


public class Q10 extends Fragment {
    SignUpViewModel viewModel;
    TextView heightValue, unitLabel;
    EditText weightValue, age;

    Slider slider;
    RadioButton ftButton, cmButton;
    Button nextButton;


    boolean isCM = true;

    public Q10() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);

        heightValue = view.findViewById(R.id.heightValue);
        unitLabel = view.findViewById(R.id.unitLabel);
        slider = view.findViewById(R.id.heightSlider);
        ftButton = view.findViewById(R.id.ftButton);
        cmButton = view.findViewById(R.id.cmButton);
        nextButton = view.findViewById(R.id.nextButton);
        weightValue = view.findViewById(R.id.weightValue);
        age = view.findViewById(R.id.age);


        slider.addOnChangeListener((slider, value, fromUser) -> {


            heightValue.setText(String.format("%.1f", slider.getValue()));


        });

        cmButton.setOnClickListener(v -> {


            isCM = true;
            unitLabel.setText("cm");

            slider.setValueFrom(100);
            slider.setValueTo(220);

            slider.setValue(180);

        });

        ftButton.setOnClickListener(v -> {
            isCM = false;
            unitLabel.setText("ft");

            slider.setValueFrom(3);
            slider.setValueTo(7);

            slider.setValue(5.00F);


        });


        nextButton.setOnClickListener(v -> {
             weightValue = view.findViewById(R.id.weightValue);
             age = view.findViewById(R.id.age);


            if (weightValue != null) {

                String weightStr = weightValue.getText().toString().trim();

                if (!weightStr.isEmpty()) {
                    try {
                        int weight = Integer.parseInt(weightStr);

                        if (weight > 5) {  
                            viewModel.weight = weight;
                        }else{

                            Toast.makeText(getContext(), "Enter valid Weight", Toast.LENGTH_SHORT).show();
                        } 

                    } catch (NumberFormatException e) {
                        
                        viewModel.weight = 0;
                    }
                }
            }else {

                Toast.makeText(getContext(), "Enter the weight", Toast.LENGTH_SHORT).show();

            }

            if (age != null) {

                String ageStr = age.getText().toString().trim();

                if (!ageStr.isEmpty()) {
                    try {
                        int ageInt = Integer.parseInt(ageStr);

                        if (ageInt > 10) {
                            viewModel.age = ageInt;
                        }else{

                            Toast.makeText(getContext(), "You're too young", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NumberFormatException e) {

                        viewModel.age = 0;
                    }
                }
            }else {

                Toast.makeText(getContext(), "Enter your age", Toast.LENGTH_SHORT).show();

            }

            if (unitLabel.getText().toString().equals("ft")) {
                viewModel.heightft = slider.getValue();

            } else if (unitLabel.getText().toString().equals("cm")) {
                viewModel.heightcm = slider.getValue();
            }

            int height = (int) slider.getValue();

            Toast.makeText(getContext(),
                    "Height: " + height + " " + unitLabel.getText(),
                    Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.qContainer, new Email())
                        .addToBackStack(null)
                        .commit();


            }, 200);

        });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q10, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityQuestion1 activity = (ActivityQuestion1) getActivity();
        if (activity != null) {

            activity.updateProgress(10);

        }

    }
}