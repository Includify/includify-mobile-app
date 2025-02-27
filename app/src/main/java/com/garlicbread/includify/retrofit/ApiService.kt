package com.garlicbread.includify.retrofit

import com.garlicbread.includify.models.request.AppointmentRequest
import com.garlicbread.includify.models.request.LoginRequest
import com.garlicbread.includify.models.request.UserRequest
import com.garlicbread.includify.models.response.Appointment
import com.garlicbread.includify.models.response.Organisation
import com.garlicbread.includify.models.response.Resource
import com.garlicbread.includify.models.response.User
import com.garlicbread.includify.models.response.UserCategory
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("registration/login")
    fun login(@Body userCredentials: LoginRequest): Call<String>

    @GET("registration")
    fun fetchUserDetails(@Header("Authorization") accessToken: String): Call<User>

    @GET("organisation/all")
    fun fetchAllOrganisations(): Call<List<Organisation>>

    @GET("organisation/{id}")
    fun fetchOrganisationById(@Path("id") organisationId: String): Call<Organisation>

    @GET("resource/{id}")
    fun fetchResourceById(@Path("id") resourceId: String): Call<Resource>

    @GET("appointment/user")
    fun fetchAllAppointments(@Header("Authorization") accessToken: String): Call<List<Appointment>>

    @POST("appointment")
    fun createAppointment(@Header("Authorization") accessToken: String, @Body appointmentRequest: AppointmentRequest): Call<Appointment>

    @POST("registration/create")
    fun createUser(@Body userRequest: UserRequest): Call<User>

    @GET("registration/category/all")
    fun getAllUserCategories(): Call<List<UserCategory>>
}
