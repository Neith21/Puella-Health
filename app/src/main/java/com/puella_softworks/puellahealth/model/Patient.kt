package com.puella_softworks.puellahealth.model
import com.google.gson.annotations.SerializedName

data class Patient(
    val id: Int? = null, // Puede ser null si es un nuevo paciente antes de crearse
    @SerializedName("patient_first_name") val firstName: String,
    @SerializedName("patient_last_name") val lastName: String,
    @SerializedName("patient_birth_date") val birthDate: String, // Formato YYYY-MM-DD
    @SerializedName("patient_age") val age: Int,
    @SerializedName("patient_email") val email: String,
    @SerializedName("patient_phone") val phone: String,
    @SerializedName("patient_gender") val gender: String,
    @SerializedName("record_active") val isActive: Int
)