package com.puella_softworks.puellahealth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btnGoMeasure).setOnClickListener {
            val intent = Intent(this, PatientListActivity::class.java)
            intent.putExtra("IS_SELECTION_MODE", true)
            startActivity(intent)
        }
        findViewById<View>(R.id.btnGoPatients).setOnClickListener {
            startActivity(Intent(this, PatientListActivity::class.java))
        }
        findViewById<View>(R.id.btnGoProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}