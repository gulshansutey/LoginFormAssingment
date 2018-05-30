package com.technophile.userformassignment.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppController {

    private static Retrofit retrofit = null;

    public static ApiInterface getRetrofitInstance(){
        return AppController.getClient().create(ApiInterface.class);
    }

    public static Retrofit getClient() {
        if (retrofit==null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiInterface.BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())// RxJavaCallAdapterFactory to support service methods return type
                    .addConverterFactory(GsonConverterFactory.create(gson))// For serialization and deserialization using Gson
                    .build();
        }
        return retrofit;
    }

}
