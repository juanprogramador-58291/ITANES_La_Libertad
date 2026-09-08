package com.example.itanes_la_libertad.data.remote.retrofit;

import com.example.itanes_la_libertad.data.remote.api.ItanesApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://raw.githubusercontent.com/juanprogramador-58291/ITANES_La_Libertad/refs/heads/main/";
    private static Retrofit retrofit = null;

    public static ItanesApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ItanesApiService.class);
    }
}
