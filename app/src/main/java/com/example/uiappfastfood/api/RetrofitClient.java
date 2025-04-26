package com.example.uiappfastfood.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

<<<<<<< HEAD
    private static final String BASE_URL = "http://192.168.1.22:8080/";
    //private static final String BASE_URL = "http://10.0.2.2:8080/";
=======
    public static final String BASE_URL = "http://192.168.17.145:8080/";
>>>>>>> fa5a889b211b2f2a74e98bf1560ff8b7fb04b994

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getRetrofitInstance(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
