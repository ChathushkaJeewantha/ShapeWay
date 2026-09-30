package lk.jiat.shapeway.network;

import lk.jiat.shapeway.models.ResetPasswordRequest;
import lk.jiat.shapeway.models.ResetPasswordResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FirebaseAuthApi {

    @POST("accounts:resetPassword")
    Call<ResetPasswordResponse> verifyResetCode(
            @Query("key") String apiKey,
            @Body ResetPasswordRequest request
    );

    @POST("accounts:resetPassword")
    Call<ResetPasswordResponse> confirmPasswordReset(
            @Query("key") String apiKey,
            @Body ResetPasswordRequest request
    );
}