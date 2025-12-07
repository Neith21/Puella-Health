package com.puella_softworks.puellahealth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puella_softworks.puellahealth.adapter.PatientAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.puella_softworks.puellahealth.model.Patient
import com.puella_softworks.puellahealth.network.RetrofitClient

class PatientListActivity : AppCompatActivity() {

    private lateinit var adapter: PatientAdapter
    private var patientList = mutableListOf<Patient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        val rvPatients = findViewById<RecyclerView>(R.id.rvPatients)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddPatient)

        adapter = PatientAdapter(patientList) { patient ->
            val intent = Intent(this, PatientDetailActivity::class.java)
            intent.putExtra("PATIENT_DATA", patient)
            startActivity(intent)
        }

        rvPatients.layoutManager = LinearLayoutManager(this)
        rvPatients.adapter = adapter

        fabAdd.setOnClickListener {
            startActivity(Intent(this, PatientFormActivity::class.java))
        }

        fetchPatients()
    }

    override fun onResume() {
        super.onResume()
        fetchPatients()
    }

    private fun fetchPatients() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = RetrofitClient.getApiService(this@PatientListActivity)
                val response = service.getPatients()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val patients = response.body()
                        if (patients != null) {
                            patientList.clear()
                            patientList.addAll(patients)
                            adapter.notifyDataSetChanged() // Refresca la UI
                        }
                    } else {
                        Toast.makeText(this@PatientListActivity, "Error al cargar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PatientListActivity, "Fallo de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}