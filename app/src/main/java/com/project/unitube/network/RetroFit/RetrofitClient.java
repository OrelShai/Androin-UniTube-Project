package com.project.unitube.network.RetroFit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class to manage the Retrofit instance.
 */
public class RetrofitClient {
    private static final String BASE_URL = "mongodb://localhost:27017/unitube";
    private static Retrofit retrofit = null;

    /**
     * Returns the Retrofit instance.
     *
     * @return Retrofit instance
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}