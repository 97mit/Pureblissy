package com.stimednp.roommvvm.di

import com.pureblissy.android.data.model.LoggedInUser
import com.pureblissy.android.network.ApiInterface
import com.pureblissy.android.ui.Activities.login.LoggedInUserView
import retrofit2.Response
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    val apiInterface: ApiInterface
) {

    suspend fun login(email: String, password: String): Response<LoggedInUser> {
        return apiInterface.userLogin(email, password)
    }

}