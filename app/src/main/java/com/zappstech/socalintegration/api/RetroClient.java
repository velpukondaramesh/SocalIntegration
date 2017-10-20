package com.zappstech.socalintegration.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * @author Pratik Butani
 */
public class RetroClient {

    /********
     * URLS
     *******/
    private static final String ROOT_URL = "http://192.168.0.103/nareshbangi/public/";
    //private static final String ROOT_URL = "http://telugucenimaentertainment.com/TKR/tkr/public/";

    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}
