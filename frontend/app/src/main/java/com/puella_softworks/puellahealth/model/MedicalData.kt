package com.puella_softworks.puellahealth.model

import com.google.gson.annotations.SerializedName

data class MedicalData(
    val id: Int? = null,

    @SerializedName("patient_height_cm") val height: Double,
    @SerializedName("patient_weight_kg") val weight: Double,
    @SerializedName("patient_blood_type") val bloodType: String,
    @SerializedName("patient_allergies") val allergies: String,
    @SerializedName("patient_existing_conditions") val conditions: String,

    @SerializedName("patient") val patientId: Int
)