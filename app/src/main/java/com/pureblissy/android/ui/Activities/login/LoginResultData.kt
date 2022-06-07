package com.pureblissy.android.ui.Activities.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResultData(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)