package com.puella_softworks.puellahealth.adapter

import android.graphics.Color
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(records[position], onItemClick)
    }

    override fun getItemCount(): Int = records.size

    fun updateList(newRecords: List<HealthRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvRecordDate)
        private val tvName: TextView = itemView.findViewById(R.id.tvPatientName)
        private val tvPressure: TextView = itemView.findViewById(R.id.tvPressure)

        fun bind(record: HealthRecord, clickListener: (HealthRecord) -> Unit) {
            tvDate.text = record.date

            tvName.text = record.patientName ?: "Paciente #${record.patientId}"

            tvPressure.text = "Presión: ${record.systolic}/${record.diastolic} mmHg"

            if (record.systolic > 140 || record.diastolic > 90) {
                tvPressure.setTextColor(Color.RED)
            } else {
                tvPressure.setTextColor(Color.BLACK)
            }

            itemView.setOnClickListener { clickListener(record) }
        }
    }
}