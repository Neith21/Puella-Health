package com.puella_softworks.puellahealth.network
import com.puella_softworks.puellahealth.model.HealthRecord
import com.puella_softworks.puellahealth.model.LoginResponse
import com.puella_softworks.puellahealth.model.MedicalData
import com.puella_softworks.puellahealth.model.Patient
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/login/")
    suspend fun login(@Body credentials: Map<String, String>): Response<LoginResponse>

    //RUTAS PARA LOS DATOS GENERALES DEL PACIENTE
    @GET("api/patients/")
    suspend fun getPatients(): Response<List<Patient>>

    @POST("api/patients/")
    suspend fun createPatient(@Body patient: Patient): Response<Patient>

    @PUT("api/patients/{id}/")
    suspend fun updatePatient(@Path("id") id: Int, @Body patient: Patient): Response<Patient>

    //RUTAS PRA LOS DATOS CLÍNICOS
    @GET("api/medical_data/{patientId}/")
    suspend fun getMedicalData(@Path("patientId") patientId: Int): Response<MedicalData>

    @POST("api/medical_data/")
    suspend fun createMedicalData(@Body data: MedicalData): Response<MedicalData>

    @PUT("api/medical_data/{id}/")
    suspend fun updateMedicalData(@Path("id") id: Int, @Body data: MedicalData): Response<MedicalData>

    //REGISTRO DE PRESIÓN
    @GET("api/health_records/")
    suspend fun getHealthRecords(@Query("patient_id") patientId: Int): Response<List<HealthRecord>>

    @POST("api/health_records/")
    suspend fun createHealthRecord(@Body record: HealthRecord): Response<HealthRecord>

    //PERFIL
    @POST("api/change_password/")
    suspend fun changePassword(@Body passwords: Map<String, String>): Response<Void>
}