package com.project.unitube.network.RetroFit;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Singleton class to manage the Retrofit instance.
 */
public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.1.223:8200/";
    private static Retrofit retrofit = null;

    /**
     * Returns the Retrofit instance.
     *
     * @return Retrofit instance
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS) // Increase connection timeout
                            .readTimeout(30, TimeUnit.SECONDS) // Increase read timeout
                            .writeTimeout(30, TimeUnit.SECONDS) // Increase write timeout
                            .build();

                    retrofit = new Retrofit.Builder().
                            baseUrl(BASE_URL)
                            .client(okHttpClient)
                            .callbackExecutor(Executors.newSingleThreadExecutor())
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }
}


