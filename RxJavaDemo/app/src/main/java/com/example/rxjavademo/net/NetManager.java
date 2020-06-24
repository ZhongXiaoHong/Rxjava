package com.example.rxjavademo.net;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetManager {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .build();

    private static NetManager INSTANCE;

    private NetManager() {

    }

    public static NetManager getInstance() {

        if (INSTANCE == null) {
            synchronized (NetManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetManager();
                }
            }

        }


        return INSTANCE;
    }

   public  <S> S createService(Class<S> clazz) {
        return retrofit.create(clazz);
    }


}
