package com.technophile.userformassignment.network;

import com.technophile.userformassignment.model.Questions;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

public interface ApiInterface {


    String  BASE_URL ="https://api.wheelstreet.com/";


    @GET()
    Observable<Questions> fetchFromUrl(@Url String url);

    @POST("v1/test/answers")
    Observable<ResponseBody> postJson(@Body String body);


}
