package com.savvy.hrmsnewapp.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient mInstance;
    private final Retrofit retrofit;

    private RetrofitClient() {

        OkHttpClient client = new OkHttpClient.Builder().writeTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel((HttpLoggingInterceptor.Level.HEADERS));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient debugClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(URLConstant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .client(debugClient)
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public ApiInterface getApi() {
        return retrofit.create(ApiInterface.class);
    }

}

