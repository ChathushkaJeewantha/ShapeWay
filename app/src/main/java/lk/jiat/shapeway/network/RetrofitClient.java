package lk.jiat.shapeway.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://identitytoolkit.googleapis.com/v1/";
    private static Retrofit retrofit;

    public static FirebaseAuthApi getAuthApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(FirebaseAuthApi.class);
    }
}