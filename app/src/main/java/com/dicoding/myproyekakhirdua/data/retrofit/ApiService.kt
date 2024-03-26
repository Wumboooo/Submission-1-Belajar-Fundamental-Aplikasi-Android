package com.dicoding.myproyekakhirdua.data.retrofit

import com.dicoding.myproyekakhirdua.data.response.DetailUserResponse
import com.dicoding.myproyekakhirdua.data.response.GithubResponse
import com.dicoding.myproyekakhirdua.data.response.ItemsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search/users")
    fun searchListUsers(
        @Query("q") q: String,
        @Query("per_page") perPage: Int
    ): Call<GithubResponse>

    @GET("users/{username}")
    fun searchDetailUser(
        @Path("username") username: String
    ): Call<DetailUserResponse>

    @GET("users/{username}/followers")
    fun getFollowers(@Path("username") username: String): Call<List<ItemsItem>>
    @GET("users/{username}/following")
    fun getFollowing(@Path("username") username: String): Call<List<ItemsItem>>
}