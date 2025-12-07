package com.puella_softworks.puellahealth.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puella_softworks.puellahealth.R
import com.puella_softworks.puellahealth.model.HealthRecord

class HealthRecordAdapter(
    private var records: List<HealthRecord>,
    private val onItemClick: (HealthRecord) -> Unit
) : RecyclerView.Adapter<HealthRecordAdapter.RecordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        holder.bind(record, onItemClick)
    }

    override fun getItemCount(): Int = records.size

    fun updateList(newRecords: List<HealthRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvRecordDate)
        private val tvPatientName: TextView = itemView.findViewById(R.id.tvPatientName)

        fun bind(record: HealthRecord, clickListener: (HealthRecord) -> Unit) {
            tvDate.text = "Fecha: ${record.date}"

            tvPatientName.text = record.patientName ?: "Paciente #${record.patientId}"

            itemView.setOnClickListener { clickListener(record) }
        }
    }
}