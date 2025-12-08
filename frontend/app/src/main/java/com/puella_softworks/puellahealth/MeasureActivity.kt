package com.puella_softworks.puellahealth

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puella_softworks.puellahealth.adapter.HealthRecordAdapter
import com.puella_softworks.puellahealth.model.HealthRecord
import com.puella_softworks.puellahealth.model.Patient
import com.puella_softworks.puellahealth.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class MeasureActivity : AppCompatActivity() {

    private lateinit var adapter: HealthRecordAdapter
    private var recordList = mutableListOf<HealthRecord>()
    private var currentPatientId: Int = -1
    private var currentPatientName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)

        val patient = intent.getSerializableExtra("PATIENT_DATA") as? Patient
        if (patient == null) {
            Toast.makeText(this, "Error: No se seleccionó paciente", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        currentPatientId = patient.id ?: -1
        currentPatientName = "${patient.firstName} ${patient.lastName}"
        title = "Medición: ${patient.firstName}"

        val rvRecords = findViewById<RecyclerView>(R.id.rvRecords)
        adapter = HealthRecordAdapter(recordList) { record ->
            showDetailDialog(record)
        }
        rvRecords.layoutManager = LinearLayoutManager(this)
        rvRecords.adapter = adapter

        val btnSimulate = findViewById<Button>(R.id.btnSimulate)
        val btnBluetooth = findViewById<Button>(R.id.btnConnectWatch)

        btnSimulate.setOnClickListener {
            simulateDataAndSave()
        }

        btnBluetooth.setOnClickListener {
            // TODO: AQUÍ IRÁ LA LÓGICA DE CONEXIÓN BLE

            Toast.makeText(this, "No hay dispositivo conectado", Toast.LENGTH_SHORT).show()
        }
        fetchHistory()
    }

    private fun fetchHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = RetrofitClient.getApiService(this@MeasureActivity)
                val response = service.getHealthRecords(currentPatientId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        recordList.clear()
                        val recordsFromApi = response.body()!!
                        recordsFromApi.forEach { record ->
                            record.patientName = currentPatientName
                        }
                        recordList.addAll(response.body()!!)
                        adapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MeasureActivity, "Error al cargar historial", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun simulateDataAndSave() {
        val randomSystolic = Random.nextInt(110, 160)
        val randomDiastolic = Random.nextInt(70, 100)

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val simulatedRecord = HealthRecord(
            date = currentDate,
            systolic = randomSystolic,
            diastolic = randomDiastolic,
            medicalNotes = "Lectura simulada automáticamente desde la App.",
            patientId = currentPatientId,
            patientName = currentPatientName
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = RetrofitClient.getApiService(this@MeasureActivity)
                val response = service.createHealthRecord(simulatedRecord)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MeasureActivity, "¡Medición Guardada!", Toast.LENGTH_SHORT).show()
                        fetchHistory()
                    } else {
                        val error = response.errorBody()?.string()
                        Toast.makeText(this@MeasureActivity, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MeasureActivity, "Fallo de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDetailDialog(record: HealthRecord) {
        AlertDialog.Builder(this)
            .setTitle("Detalle del Registro")
            .setMessage("""
                Fecha: ${record.date}
                Presión: ${record.systolic}/${record.diastolic}
                
                Notas Médicas:  
                ${record.medicalNotes}
                
                --- DIAGNÓSTICO (IA) ---
                ${record.diagnosis ?: "Pendiente..."}
            """.trimIndent())
            .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}