package com.puella_softworks.puellahealth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.puella_softworks.puellahealth.model.LoginUserDataRequest
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
            val user = etUser.text.toString()
            val pass = etPass.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Llena los campos, por favor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sessionManager = SessionManager(this)
            sessionManager.clearData()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val api = RetrofitClient.getApiService(this@LoginActivity)
                    val session = SessionManager(this@LoginActivity)

                    val tokenCredentials = mapOf("username" to user, "password" to pass)
                    val tokenResponse = api.getToken(tokenCredentials)

                    if (tokenResponse.isSuccessful && tokenResponse.body() != null) {
                        val token = tokenResponse.body()!!.token
                        session.saveAuthToken(token)

                        val userInfoRequest = LoginUserDataRequest(identifier = user, password = pass)
                        val userResponse = api.getUserInfo(userInfoRequest)

                        if (userResponse.isSuccessful && userResponse.body() != null) {
                            val userData = userResponse.body()!!.user
                            session.saveUserDetails(userData)

                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@LoginActivity, "Bienvenido, ${userData.firstName}", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@LoginActivity, "Token OK, pero error al cargar perfil", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        }

                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()

                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        findViewById<Button>(R.id.btnSignUp).setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://neith21.github.io/register.html"))
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("Santo Padre", "Error al abrir URL: ${e.message}")
            }
        }
    }
}