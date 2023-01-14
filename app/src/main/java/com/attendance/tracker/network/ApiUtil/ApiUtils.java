package com.attendance.tracker.network.ApiUtil;

import android.util.Log;

import com.attendance.tracker.BuildConfig;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.RetroClient;


public class ApiUtils {
    public static APIService getApiService(String baseURL){


        if (BuildConfig.DEBUG){
            Log.d("responsedata", baseURL);
        }
        return RetroClient.getClient(baseURL)
                .create(APIService.class);
    }

    /*public static AnotherRetroService getAboutUsService() {
        return RetroClient.getAnotherClient("http://artificial-soft.com").create(AnotherRetroService.class);
    }

    public static AnotherRetroService getRssService (String baseURL){
        Log.d("responsedata", baseURL);
        return RetroClient.getRssClient(baseURL).create(AnotherRetroService.class);
    }*/
}
