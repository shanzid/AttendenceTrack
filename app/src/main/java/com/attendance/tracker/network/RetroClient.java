package com.attendance.tracker.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetroClient {
    public static Retrofit retrofit = null;

    public static Retrofit getClient(String bsURL) {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(bsURL)
                   // .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        } else {
            if (!retrofit.baseUrl().equals(bsURL)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(bsURL)
                      //  .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        return retrofit;
    }

    private static Retrofit anotherRetrofit = null;

    public static Retrofit getAnotherClient(String baseUrl) {
        if (anotherRetrofit == null) {
            anotherRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)

                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return anotherRetrofit;
    }
    private static Retrofit rssRetrofit = null;

    public static Retrofit getRssClient(String baseUrl) {
        if (rssRetrofit == null) {
            rssRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
        }
        return rssRetrofit;
    }


}
