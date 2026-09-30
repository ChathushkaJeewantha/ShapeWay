package lk.jiat.shapeway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;

import lk.jiat.shapeway.BuildConfig;
import lk.jiat.shapeway.R;
import lk.jiat.shapeway.models.ErrorResponse;
import lk.jiat.shapeway.models.ResetPasswordRequest;
import lk.jiat.shapeway.models.ResetPasswordResponse;
import lk.jiat.shapeway.network.FirebaseAuthApi;
import lk.jiat.shapeway.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetCodeVerificationActivity extends AppCompatActivity {

    private EditText etResetEmail, etVerificationCode, etNewPassword, etConfirmPassword;
    private Button btnVerifyAndReset;
    private FirebaseAuthApi authApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_code_verification);

        etResetEmail = findViewById(R.id.etResetEmail);
        etVerificationCode = findViewById(R.id.etVerificationCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnVerifyAndReset = findViewById(R.id.btnVerifyAndReset);

        authApi = RetrofitClient.getAuthApi();

        String email = getIntent().getStringExtra("email");
        if (email != null) {
            etResetEmail.setText(email);
        }

        btnVerifyAndReset.setOnClickListener(v -> verifyAndResetPassword());
    }

    private void verifyAndResetPassword() {
        String email = etResetEmail.getText().toString().trim();
        String code = etVerificationCode.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etResetEmail.setError("Email is required");
            etResetEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etResetEmail.setError("Enter valid email");
            etResetEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(code)) {
            etVerificationCode.setError("Verification code is required");
            etVerificationCode.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        btnVerifyAndReset.setEnabled(false);
        btnVerifyAndReset.setText("Verifying...");

        // Step 1: verify code first
        ResetPasswordRequest verifyRequest = new ResetPasswordRequest(code);

        authApi.verifyResetCode(BuildConfig.FIREBASE_WEB_API_KEY, verifyRequest)
                .enqueue(new Callback<ResetPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String responseEmail = response.body().getEmail();

                            if (responseEmail != null && responseEmail.equalsIgnoreCase(email)) {
                                confirmPasswordReset(code, newPassword);
                            } else {
                                btnVerifyAndReset.setEnabled(true);
                                btnVerifyAndReset.setText("Verify & Update Password");
                                Toast.makeText(ResetCodeVerificationActivity.this,
                                        "This code does not match the entered email",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                        btnVerifyAndReset.setEnabled(true);
                        btnVerifyAndReset.setText("Verify & Update Password");
                        Toast.makeText(ResetCodeVerificationActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void confirmPasswordReset(String code, String newPassword) {
        ResetPasswordRequest confirmRequest = new ResetPasswordRequest(code, newPassword);

        authApi.confirmPasswordReset(BuildConfig.FIREBASE_WEB_API_KEY, confirmRequest)
                .enqueue(new Callback<ResetPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                        btnVerifyAndReset.setEnabled(true);
                        btnVerifyAndReset.setText("Verify & Update Password");

                        if (response.isSuccessful()) {
                            Toast.makeText(ResetCodeVerificationActivity.this,
                                    "Password updated successfully",
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(ResetCodeVerificationActivity.this, SignIn.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                        btnVerifyAndReset.setEnabled(true);
                        btnVerifyAndReset.setText("Verify & Update Password");
                        Toast.makeText(ResetCodeVerificationActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleErrorResponse(Response<?> response) {
        btnVerifyAndReset.setEnabled(true);
        btnVerifyAndReset.setText("Verify & Update Password");

        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                ErrorResponse errorResponse = new Gson().fromJson(errorJson, ErrorResponse.class);

                if (errorResponse != null &&
                        errorResponse.getError() != null &&
                        errorResponse.getError().getMessage() != null) {

                    String firebaseMessage = mapFirebaseError(errorResponse.getError().getMessage());
                    Toast.makeText(this, firebaseMessage, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
    }

    private String mapFirebaseError(String error) {
        switch (error) {
            case "INVALID_OOB_CODE":
                return "Invalid or expired verification code";
            case "EXPIRED_OOB_CODE":
                return "Verification code has expired";
            case "USER_DISABLED":
                return "This account is disabled";
            case "WEAK_PASSWORD : Password should be at least 6 characters":
            case "WEAK_PASSWORD":
                return "Weak password. Use at least 6 characters";
            default:
                return error;
        }
    }
}