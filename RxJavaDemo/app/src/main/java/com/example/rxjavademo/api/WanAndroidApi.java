package com.example.rxjavademo.api;

import com.example.rxjavademo.bean.LoginResponse;
import com.example.rxjavademo.bean.ProjectBean;
import com.example.rxjavademo.bean.ProjectItem;
import com.example.rxjavademo.bean.RegisterResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WanAndroidApi {

    @GET("project/tree/json")
    Observable<ProjectBean> getProjectCategory();

    @GET("project/list/{page}/json")
    Observable<ProjectItem> getItemsInCategory(@Path("page") int page, @Query("cid") int cid);



    @POST("/user/register")
    @FormUrlEncoded
    Observable<RegisterResponse> register(@Field("username") String username, @Field("password") String password, @Field("repassword") String repassword);


    @POST("/user/login")
    @FormUrlEncoded
    Observable<LoginResponse> login(@Field("username") String username, @Field("password") String password);



}
