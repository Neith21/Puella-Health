package com.puella_softworks.puellahealth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.puella_softworks.puellahealth.model.ChangePasswordRequest
import com.puella_softworks.puellahealth.network.RetrofitClient
import com.puella_softworks.puellahealth.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val session = SessionManager(this)

        val tvUser = findViewById<TextView>(R.id.tvUsernameProfile)
        val etOldPass = findViewById<EditText>(R.id.etOldPass)
        val etNewPass = findViewById<EditText>(R.id.etNewPass)
        val etConfirmPass = findViewById<EditText>(R.id.etConfirmPass)
        val btnChange = findViewById<Button>(R.id.btnChangePass)

        tvUser.text = "Hola, ${session.getUserName()}"

        btnChange.setOnClickListener {
            val oldPass = etOldPass.text.toString()
            val newPass = etNewPass.text.toString()
            val confirmPass = etConfirmPass.text.toString()
            val userId = session.getUserId()

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                etConfirmPass.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            if (userId == -1) {
                Toast.makeText(this, "Error de sesión. Vuelve a iniciar login.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            changePassword(userId, oldPass, newPass, confirmPass)
        }
    }

    private fun changePassword(uid: Int, old: String, new: String, confirm: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = ChangePasswordRequest(uid, old, new, confirm)
                val service = RetrofitClient.getApiService(this@ProfileActivity)

                val response = service.changePassword(request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val resp = response.body()!!
                        if (resp.status == "ok") {
                            Toast.makeText(this@ProfileActivity, resp.message, Toast.LENGTH_LONG).show()

                            findViewById<EditText>(R.id.etOldPass).text.clear()
                            findViewById<EditText>(R.id.etNewPass).text.clear()
                            findViewById<EditText>(R.id.etConfirmPass).text.clear()
                        } else {
                            Toast.makeText(this@ProfileActivity, "Error: ${resp.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "Error al actualizar. Verifica tu contraseña actual.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProfileActivity, "Fallo de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}