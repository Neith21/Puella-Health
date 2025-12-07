package com.puella_softworks.puellahealth.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HealthRecord(
    val id: Int? = null,
    @SerializedName("record_date") val date: String,
    @SerializedName("preassure_systolic") val systolic: Int,
    @SerializedName("preassure_diastolic") val diastolic: Int,
    @SerializedName("medical_data") val medicalNotes: String,
    @SerializedName("record_diagnosis") val diagnosis: String? = null,
    @SerializedName("patient") val patientId: Int,

    var patientName: String? = null
) : Serializable