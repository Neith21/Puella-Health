package com.puella_softworks.puellahealth.utils

import android.content.Context
import android.content.SharedPreferences
import com.puella_softworks.puellahealth.model.UserDetail

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
    }

    fun saveUserDetails(user: UserDetail) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, user.id)
        editor.putString(USER_NAME, user.firstName)
        editor.putString(USER_EMAIL, user.email)
        editor.apply()
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.commit()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun getUserId(): Int {
        return prefs.getInt(USER_ID, -1)
    }

    fun getUserName(): String? = prefs.getString(USER_NAME, "Usuario")

    fun clearData() {
        prefs.edit().clear().apply()
    }
}