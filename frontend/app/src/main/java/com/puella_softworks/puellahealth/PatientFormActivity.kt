package com.puella_softworks.puellahealth

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.puella_softworks.puellahealth.model.Patient
import com.puella_softworks.puellahealth.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_form)

        val etName = findViewById<EditText>(R.id.etFormName)
        val etLastName = findViewById<EditText>(R.id.etFormLastName)
        val etBirth = findViewById<EditText>(R.id.etFormBirth)
        val etEmail = findViewById<EditText>(R.id.etFormEmail)
        val etPhone = findViewById<EditText>(R.id.etFormPhone)
        val etGender = findViewById<AutoCompleteTextView>(R.id.etFormGender)
        val btnSave = findViewById<Button>(R.id.btnFormSave)

        val genderOptions = listOf(
            "Masculino",
            "Femenino",
            "Prefiero No Decirlo"
        )

        val genderAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            genderOptions
        )
        etGender.setAdapter(genderAdapter)

        etBirth.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                { _, y, m, d ->
                    val selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                    etBirth.setText(selectedDate)
                },
                year, month, day
            ).show()
        }

        btnSave.setOnClickListener {
            val newPatient = Patient(
                firstName = etName.text.toString(),
                lastName = etLastName.text.toString(),
                birthDate = etBirth.text.toString(),
                email = etEmail.text.toString(),
                phone = etPhone.text.toString(),
                gender = etGender.text.toString()
            )
            createPatientInBackend(newPatient)
        }
    }

    private fun createPatientInBackend(patient: Patient) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = RetrofitClient.getApiService(this@PatientFormActivity)
                val response = service.createPatient(patient)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PatientFormActivity, "¡Paciente registrado!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@PatientFormActivity, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PatientFormActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}