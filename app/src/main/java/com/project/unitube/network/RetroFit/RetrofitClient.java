package com.project.unitube.network.RetroFit;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import com.project.unitube.utils.manager.UserManager; // Import UserManager to access the token

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
                            .addInterceptor(chain -> {
                                Request original = chain.request();
                                Request.Builder builder = original.newBuilder();

                                // Fetch token from UserManager
                                String token = UserManager.getInstance().getToken();

                                // Add the Authorization header if the token is available
                                if (token != null && !token.isEmpty()) {
                                    builder.header("Authorization", "Bearer " + token); // Prepend Bearer
                                }

                                Request request = builder.build();
                                return chain.proceed(request);
                            })
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
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

    // get the base url
    public static String getBaseUrl() {
        return BASE_URL;
    }
}


