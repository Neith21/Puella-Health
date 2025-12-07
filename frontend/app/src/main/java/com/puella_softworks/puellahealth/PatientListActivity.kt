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

class PatientListActivity : AppCompatActivity() {
    private lateinit var adapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        val rvPatients = findViewById<RecyclerView>(R.id.rvPatients)

        adapter = PatientAdapter(emptyList()) { selectedPatient ->
            val intent = Intent(this, PatientDetailActivity::class.java)
            intent.putExtra("PATIENT_ID", selectedPatient.id)
            startActivity(intent)
        }

        rvPatients.layoutManager = LinearLayoutManager(this)
        rvPatients.adapter = adapter
    }
}