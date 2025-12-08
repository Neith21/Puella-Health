package com.puella_softworks.puellahealth

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.puella_softworks.puellahealth.model.MedicalData
import com.puella_softworks.puellahealth.model.Patient
import com.puella_softworks.puellahealth.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientDetailActivity : AppCompatActivity() {

    private var currentPatientId: Int = -1

    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var etBlood: EditText
    private lateinit var etAllergies: EditText
    private lateinit var etConditions: EditText
    private lateinit var btnSave: Button
    private lateinit var spinnerBloodType: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)

        val patient = intent.getSerializableExtra("PATIENT_DATA") as? Patient

        if (patient == null) {
            Toast.makeText(this, "Error al cargar paciente", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentPatientId = patient.id ?: -1
        title = "${patient.firstName} ${patient.lastName}"

        initViews()

        fetchMedicalDetails(currentPatientId)

        btnSave.setOnClickListener {
            saveMedicalData()
        }
    }

    private fun initViews() {
        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)
        spinnerBloodType = findViewById(R.id.spinnerBloodType)
        etAllergies = findViewById(R.id.etAllergies)
        etConditions = findViewById(R.id.etConditions)
        btnSave = findViewById(R.id.btnSaveMedicalData)

        setupBloodTypeSpinner()
    }

    private fun setupBloodTypeSpinner() {
        val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            bloodTypes
        )

        spinnerBloodType.adapter = adapter
    }

    private fun fetchMedicalDetails(patientId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = RetrofitClient.getApiService(this@PatientDetailActivity)
                val response = service.getMedicalData(patientId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val dataList = response.body()!!

                        if (dataList.isNotEmpty()) {
                            val data = dataList[0]
                            populateFields(data)

                            btnSave.text = "Datos Actualizados"
                            btnSave.isEnabled = false
                        } else {
                            Toast.makeText(this@PatientDetailActivity, "Datos no registrados. Por favor ingrésalos.", Toast.LENGTH_LONG).show()
                            btnSave.text = "Guardar Datos"
                            btnSave.isEnabled = true
                        }
                    } else {
                        Toast.makeText(this@PatientDetailActivity, "Error al consultar datos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PatientDetailActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateFields(data: MedicalData) {
        etHeight.setText(data.height.toString())
        etWeight.setText(data.weight.toString())
        etAllergies.setText(data.allergies)
        etConditions.setText(data.conditions)

        val types = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        val index = types.indexOf(data.bloodType)
        if (index != -1) spinnerBloodType.setSelection(index)
    }

    private fun saveMedicalData() {
        if (etHeight.text.isEmpty() || etWeight.text.isEmpty()) {
            Toast.makeText(this, "Altura y Peso son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val newData = MedicalData(
            height = etHeight.text.toString().toDouble(),
            weight = etWeight.text.toString().toDouble(),
            bloodType = spinnerBloodType.selectedItem.toString(),
            allergies = etAllergies.text.toString(),
            conditions = etConditions.text.toString(),
            patientId = currentPatientId
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = RetrofitClient.getApiService(this@PatientDetailActivity)
                val response = service.createMedicalData(newData)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PatientDetailActivity, "¡Información clínica guardada!", Toast.LENGTH_SHORT).show()
                        btnSave.isEnabled = false
                        btnSave.text = "Guardado"
                    } else {
                        val error = response.errorBody()?.string()
                        Toast.makeText(this@PatientDetailActivity, "Error al guardar: $error", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PatientDetailActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}