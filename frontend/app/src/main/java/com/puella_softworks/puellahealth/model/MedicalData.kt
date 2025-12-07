package com.puella_softworks.puellahealth.model

import com.google.gson.annotations.SerializedName

data class MedicalData(
    val id: Int? = null,
    @SerializedName("patient_height_cm") val heightCm: Double,
    @SerializedName("patient_weight_kg") val weightKg: Double,
    @SerializedName("patient_blood_type") val bloodType: String,
    @SerializedName("patient_allergies") val allergies: String,
    @SerializedName("patient_existing_conditions") val conditions: String,
    @SerializedName("record_active") val isActive: Int,
    @SerializedName("patient_id") val patientId: Int
)