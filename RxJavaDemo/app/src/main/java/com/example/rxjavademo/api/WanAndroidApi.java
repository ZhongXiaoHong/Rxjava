package com.example.rxjavademo.api;

import com.example.rxjavademo.bean.ProjectCategory;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WanAndroidApi {

    @GET("project/tree/json")
    Observable<ProjectCategory> getProjectCategory();

    @GET("project/list/{page}/json")
    Observable<ProjectCategory> getItemsInCategory(@Path("page") int page,@Query("cid") int cid);


}
