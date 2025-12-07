package com.puella_softworks.puellahealth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.puella_softworks.puellahealth.network.RetrofitClient
import com.puella_softworks.puellahealth.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etUser = findViewById<EditText>(R.id.etUsername)
        val etPass = findViewById<EditText>(R.id.etPassword)

        btnLogin.setOnClickListener {
            val credentials = mapOf(
                "username" to etUser.text.toString(),
                "password" to etPass.text.toString()
            )
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.getApiService(this@LoginActivity).login(credentials)

                    if (response.isSuccessful && response.body() != null) {
                        val token = response.body()!!.token

                        val session = SessionManager(this@LoginActivity)
                        session.saveAuthToken(token)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, "Error: Credenciales inválidas", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}