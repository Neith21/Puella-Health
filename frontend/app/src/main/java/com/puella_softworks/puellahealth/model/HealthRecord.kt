package com.puella_softworks.puellahealth.model

import com.google.gson.annotations.SerializedName

data class HealthRecord(
    val id: Int? = null,
    @SerializedName("record_date") val date: String,
    @SerializedName("preassure_systolic") val systolic: Int,
    @SerializedName("preassure_diastolic") val diastolic: Int,
    @SerializedName("record_diagnosis") val diagnosis: String? = null, // Viene del backend
    @SerializedName("record_active") val isActive: Int = 1,
    @SerializedName("patient_id") val patientId: Int
)