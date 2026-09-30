package lk.jiat.shapeway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.databinding.ActivitySignInBinding;

public class SignIn extends AppCompatActivity {


    private ActivitySignInBinding binding;
    EditText email, password;
    Button loginBtn, createAccount;
    ProgressBar progressBar;
    ImageView togglePassword;

  private FirebaseAuth mAuth;
    boolean isPasswordVisible = false;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        //EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        email = binding.email;
        password = binding.password;
        loginBtn = binding.loginBtn;
        togglePassword = binding.togglePassword;
        createAccount = binding.createAccount;

        TextView txtForgotPassword = findViewById(R.id.txtForgotPassword);

        txtForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(SignIn.this, ForgotPasswordActivity.class));
        });


        progressBar = new ProgressBar(this);
        mAuth = FirebaseAuth.getInstance();

        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            isPasswordVisible = !isPasswordVisible;
            password.setSelection(password.getText().length());
        });

        loginBtn.setOnClickListener(v -> {
            if (validate()) {
                loginUser();
            }
        });

        createAccount.setOnClickListener(view -> {

            Intent intent = new Intent(SignIn.this, ActivityQuestion1.class);
            startActivity(intent);
            finish();

        });

    }

    private void loginUser() {

        loginBtn.setEnabled(false);
        loginBtn.setText("Logging...");

        String emailText = email.getText().toString().trim();
        String passText = password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser == null) {
                            loginBtn.setEnabled(true);
                            loginBtn.setText("LOG IN");
                            Toast.makeText(SignIn.this, "User data not available. Try again.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String uid = firebaseUser.getUid();

                        FirebaseFirestore.getInstance().collection("users")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(snapshot -> {

                                    loginBtn.setEnabled(true);
                                    loginBtn.setText("LOG IN");

                                    Boolean isBlocked = snapshot.getBoolean("isBlocked");

                                    if (isBlocked != null && isBlocked) {
                                        FirebaseAuth.getInstance().signOut();
                                        Toast.makeText(SignIn.this, "Your account is blocked", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SignIn.this, "Login Success", Toast.LENGTH_SHORT).show();

                                        currentUser = firebaseUser;

                                        Intent intent = new Intent(SignIn.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    loginBtn.setEnabled(true);
                                    loginBtn.setText("LOG IN");
                                    Toast.makeText(SignIn.this, "Failed to check user status: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });

                    } else {
                        loginBtn.setEnabled(true);
                        loginBtn.setText("LOG IN");

                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error";

                        Toast.makeText(SignIn.this, "Invalid Password or Email: " + error, Toast.LENGTH_LONG).show();
                    }
                });



    }

    private boolean validate() {

        String emailText = email.getText().toString().trim();
        String passText = password.getText().toString().trim();

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

        return true;
    }

    private void showError(EditText field, String msg) {
        field.setBackgroundResource(R.drawable.bg_edittext_error);
        field.setError(msg);
    }

}