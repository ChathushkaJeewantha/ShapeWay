package lk.jiat.shapeway.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.activity.viewmodels.SignUpViewModel;


public class Email extends Fragment {

    SignUpViewModel viewModel;
    EditText email, name, password, rePassword;
    ImageView togglePassword, toggleRePassword;
    Button submitBtn;

    boolean isPasswordVisible = false;

    public Email() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity())
                .get(SignUpViewModel.class);

        email = view.findViewById(R.id.email);
        name = view.findViewById(R.id.name);
        password = view.findViewById(R.id.password);
        rePassword = view.findViewById(R.id.rePassword);
        togglePassword = view.findViewById(R.id.togglePassword);
        toggleRePassword = view.findViewById(R.id.toggleRePassword);
        submitBtn = view.findViewById(R.id.submitBtn);

        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            isPasswordVisible = !isPasswordVisible;
            password.setSelection(password.getText().length());
        });
        toggleRePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                rePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                rePassword.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            isPasswordVisible = !isPasswordVisible;
            rePassword.setSelection(rePassword.getText().length());
        });


        submitBtn.setOnClickListener(v -> {

            if (validate()) {
                submitUser();
            }



        });


    }

    private void submitUser(){
        new Handler().postDelayed(() -> {
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left,
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right
                            )
                            .replace(R.id.qContainer, new Subscription())
                            .addToBackStack(null)
                            .commit();


                }, 200);



    }

    private boolean validate() {

        String nameText = name.getText().toString().trim();
        String emailText = email.getText().toString().trim();

        String passText = password.getText().toString().trim();
        String rePassText = rePassword.getText().toString().trim();



        if (nameText.isEmpty()) {
            showError(name, "Enter email");
            return false;
        }



        if (emailText.isEmpty()) {
            showError(email, "Enter email");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            showError(email, "Invalid email");
            return false;
        }

        if (passText.length() < 6) {
            showError(password, "Minimum 6 characters");
            return false;
        }

        if (rePassText.length() < 6) {
            showError(rePassword, "Minimum 6 characters");
            return false;
        }

        viewModel.name=nameText;
        viewModel.email=emailText;
        viewModel.password=passText;
        return true;
    }
    private void showError(EditText field, String msg) {
        field.setBackgroundResource(R.drawable.bg_edittext_error);
        field.setError(msg);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email, container, false);
    }
}