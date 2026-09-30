package lk.jiat.shapeway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import lk.jiat.shapeway.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSendReset;
    private TextView txtGoCodePage;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        btnSendReset = findViewById(R.id.btnSendReset);
        txtGoCodePage = findViewById(R.id.txtGoCodePage);

        btnSendReset.setOnClickListener(v -> sendResetEmail());

        txtGoCodePage.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, ResetCodeVerificationActivity.class);
            intent.putExtra("email", etEmail.getText().toString().trim());
            startActivity(intent);
        });
    }

    private void sendResetEmail() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        btnSendReset.setEnabled(false);
        btnSendReset.setText("Sending...");

        mAuth.setLanguageCode("en");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    btnSendReset.setEnabled(true);
                    btnSendReset.setText("Send Verification Email");

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetCodeVerificationActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {
                        String msg = task.getException() != null ? task.getException().getMessage() : "Failed to send email";
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}