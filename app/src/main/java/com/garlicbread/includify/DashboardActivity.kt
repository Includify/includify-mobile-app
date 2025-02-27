package com.garlicbread.includify

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.garlicbread.includify.databinding.ActivityDashboardBinding
import com.garlicbread.includify.models.response.User
import com.garlicbread.includify.retrofit.RetrofitInstance
import com.garlicbread.includify.util.Constants.Companion.API
import com.garlicbread.includify.util.Constants.Companion.SHARED_PREF_NAME
import com.garlicbread.includify.util.Constants.Companion.SHARED_PREF_TAG
import com.garlicbread.includify.util.Constants.Companion.SHARED_PREF_USER_ID
import com.garlicbread.includify.util.HelperMethods
import com.garlicbread.includify.util.HelperMethods.Companion.getAccessToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(listOf(android.Manifest.permission.POST_NOTIFICATIONS).toTypedArray(), 1);
        }

        val sharedPreferences = getSharedPreferences(SHARED_PREF_TAG, MODE_PRIVATE)
        val name = sharedPreferences.getString(SHARED_PREF_NAME, "")

        if (name.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val accessToken = getAccessToken(this@DashboardActivity)
                    fetchDetails(accessToken)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@DashboardActivity,
                            "Server down, please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e(API, "Failure: ${e.message}")
                    }
                }
            }
        }
        else {
            binding.name.text = name
        }

        binding.exploreButton.setOnClickListener {
            val newIntent = Intent(this, OrganisationListActivity::class.java)
            startActivity(newIntent)
        }

        binding.emergencyContacts.setOnClickListener {
            val newIntent = Intent(this, EmergencyContactsActivity::class.java)
            startActivity(newIntent)
        }

        binding.medicines.setOnClickListener {
            val newIntent = Intent(this, MedicinesActivity::class.java)
            startActivity(newIntent)
        }

        binding.appointmentsButton.setOnClickListener {
            val newIntent = Intent(this, AppointmentListActivity::class.java)
            startActivity(newIntent)
        }

        binding.chatBot.setOnClickListener {
            val newIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://6b39-2603-7000-9a00-1808-65be-4d37-70d4-da45.ngrok-free.app"))
            startActivity(newIntent)
        }

        binding.signOutBtn.setOnClickListener {
            HelperMethods.signOut(this, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchDetails(accessToken: String) {
        RetrofitInstance.api.fetchUserDetails("Bearer $accessToken").enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    response.body()?.let { saveDetails(it.name, it.id) }
                    binding.name.text = response.body()?.name ?: "Includify User"
                }
                else if (response.code() == 401) {
                    HelperMethods.signOut(this@DashboardActivity)
                }
                else {
                    binding.name.text = "Includify User"
                    Log.e(API, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                binding.name.text = "Includify User"
                Log.e(API, "Failure: ${t.message}")
            }
        })
    }

    private fun saveDetails(
        name: String,
        userId: String
    ) {
        val prefs = this.getSharedPreferences(SHARED_PREF_TAG, MODE_PRIVATE)
        prefs.edit().putString(SHARED_PREF_USER_ID, userId).apply()
        prefs.edit().putString(SHARED_PREF_NAME, name).apply()
    }
}