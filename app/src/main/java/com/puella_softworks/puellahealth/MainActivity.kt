package com.puella_softworks.puellahealth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btnGoMeasure).setOnClickListener {
            startActivity(Intent(this, MeasureActivity::class.java))
        }
        findViewById<View>(R.id.btnGoPatients).setOnClickListener {
            startActivity(Intent(this, PatientListActivity::class.java))
        }
        findViewById<View>(R.id.btnGoProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}