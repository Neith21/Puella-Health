package com.puella_softworks.puellahealth.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puella_softworks.puellahealth.R
import com.puella_softworks.puellahealth.model.Patient

class PatientAdapter(
    private var patients: List<Patient>,
    private val onItemClick: (Patient) -> Unit
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        holder.bind(patient, onItemClick)
    }

    override fun getItemCount(): Int = patients.size

    fun updateList(newPatients: List<Patient>) {
        patients = newPatients
        notifyDataSetChanged()
    }

    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvFullName)
        private val tvInfo: TextView = itemView.findViewById(R.id.tvAgeSex)

        fun bind(patient: Patient, clickListener: (Patient) -> Unit) {
            tvName.text = "${patient.firstName} ${patient.lastName}"

            tvInfo.text = "${patient.age} años - ${patient.gender}"

            itemView.setOnClickListener { clickListener(patient) }
        }
    }
}