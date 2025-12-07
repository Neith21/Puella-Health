package com.puella_softworks.puellahealth.network

import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context
import com.puella_softworks.puellahealth.utils.SessionManager

class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Token $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}