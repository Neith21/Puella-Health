package com.puella_softworks.puellahealth.model

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("current_password") val currentPass: String,
    @SerializedName("new_password") val newPass: String,
    @SerializedName("confirm_password") val confirmPass: String
)

data class ChangePasswordResponse(
    val status: String,
    val message: String
)