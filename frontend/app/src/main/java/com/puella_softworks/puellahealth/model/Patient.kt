package com.puella_softworks.puellahealth.model
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Patient(
    val id: Int? = null,
    @SerializedName("patient_first_name") val firstName: String,
    @SerializedName("patient_last_name") val lastName: String,
    @SerializedName("patient_birth_date") val birthDate: String,
    @SerializedName("patient_email") val email: String,
    @SerializedName("patient_phone") val phone: String,
    @SerializedName("patient_gender") val gender: String,

    //Solo del get
    @SerializedName("patient_age") val age: Int? = null,
    @SerializedName("active") val isActive: Boolean? = true
) : Serializable