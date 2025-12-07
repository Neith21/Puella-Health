package com.puella_softworks.puellahealth.network
import com.puella_softworks.puellahealth.model.ChangePasswordRequest
import com.puella_softworks.puellahealth.model.ChangePasswordResponse
import com.puella_softworks.puellahealth.model.HealthRecord
import com.puella_softworks.puellahealth.model.LoginResponse
import com.puella_softworks.puellahealth.model.LoginUserDataRequest
import com.puella_softworks.puellahealth.model.MedicalData
import com.puella_softworks.puellahealth.model.Patient
import com.puella_softworks.puellahealth.model.TokenResponse
import com.puella_softworks.puellahealth.model.UserDataResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/v1/api-token-auth/")
    suspend fun getToken(@Body credentials: Map<String, String>): Response<TokenResponse>

    @POST("api/v1/user-control/login")
    suspend fun getUserInfo(@Body request: LoginUserDataRequest): Response<UserDataResponse>

    //RUTAS PARA LOS DATOS GENERALES DEL PACIENTE
    @GET("api/v1/patients/")
    suspend fun getPatients(): Response<List<Patient>>

    @POST("api/v1/patients/")
    suspend fun createPatient(@Body patient: Patient): Response<Patient>

    @PUT("api/patients/{id}/")
    suspend fun updatePatient(@Path("id") id: Int, @Body patient: Patient): Response<Patient>

    //RUTAS PRA LOS DATOS CLÍNICOS
    @GET("api/v1/medical-data/")
    suspend fun getMedicalData(@Query("patient_id") patientId: Int): Response<List<MedicalData>>

    @POST("api/v1/medical-data/")
    suspend fun createMedicalData(@Body data: MedicalData): Response<MedicalData>

    @PUT("api/medical_data/{id}/")
    suspend fun updateMedicalData(@Path("id") id: Int, @Body data: MedicalData): Response<MedicalData>

    //REGISTRO DE PRESIÓN
    @GET("api/v1/health-records/")
    suspend fun getHealthRecords(@Query("patient_id") patientId: Int): Response<List<HealthRecord>>

    @POST("api/v1/health-records/")
    suspend fun createHealthRecord(@Body record: HealthRecord): Response<HealthRecord>

    //PERFIL
    @POST("api/v1/user-control/change-password/")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>
}