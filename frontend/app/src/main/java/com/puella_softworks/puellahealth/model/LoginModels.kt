package com.puella_softworks.puellahealth.model

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("token") val token: String
)
data class LoginUserDataRequest(
    @SerializedName("email_or_username") val identifier: String,
    @SerializedName("password") val password: String
)
data class UserDataResponse(
    val status: String,
    val user: UserDetail
)
data class UserDetail(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("first_name") val firstName: String
)

