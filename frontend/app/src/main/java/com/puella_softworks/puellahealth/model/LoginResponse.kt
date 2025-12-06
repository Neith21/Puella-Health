package com.puella_softworks.puellahealth.model

data class LoginResponse(
    val token: String,
    val userId: Int,
    val username: String
)