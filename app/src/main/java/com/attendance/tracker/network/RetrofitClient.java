package com.attendance.tracker.network;


import com.attendance.tracker.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient myClient;
    private Retrofit retrofit;

    private RetrofitClient() {

//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//        AndroidNetworking.setParserFactory(new GsonParserFactory(gson));

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }

    public static synchronized RetrofitClient getInstance() {

        if (myClient == null) {
            myClient = new RetrofitClient();
        }
        return myClient;

    }

    public APIService getApi() {

        return retrofit.create(APIService.class);
    }


}
