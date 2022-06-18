package com.pureblissy.android.network

import com.pureblissy.android.data.model.LoggedInUser
import com.pureblissy.android.ui.Activities.login.LoggedInUserView
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String,
    ): Response<LoggedInUserView>

    @GET("v2/login")
    suspend fun userLogin(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String,
    ): Response<LoggedInUser>
}