package com.chari.money

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.serialization.Serializable
import com.voveid.sdk.VerificationResult.*
import com.voveid.sdk.Vove
import com.voveid.sdk.VoveEnvironment
import com.voveid.sdk.VoveLocale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.coroutines.flow.Flow
import androidx.activity.viewModels
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit

//@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    var PERMISSION_ALL = 1
    var textView_label: TextView? = null
    var textView_1: TextView? = null
    private val CODE_PERMISSION = 1
    var PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    private val viewModel by viewModels<RemoteViewModel>()

    @Inject
    lateinit var httpClient: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView_label = findViewById(R.id.textView_label)
        textView_1 = findViewById(R.id.textView_1)
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)


        val btn3: Button = findViewById(R.id.buttonVoveId)
        btn3.setOnClickListener { main() }
    }

    fun main() {
        val contentType = "application/json".toMediaType()
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
        }
        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sandbox.voveid.com/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(httpClient)
            .build()

        // Create API service
        val apiService = retrofit.create(VerificationService::class.java)

        // Create the request body
        val sessionRequest = TokenRequest(refId = "f0692ed4-asd2-qwe1-asd3-5a45547b2aa8")

        // Make the API call
        val call = apiService.verificationToken(sessionRequest)
        call.enqueue(object : Callback<TokenResponse> {
            override fun onResponse(
                call: Call<TokenResponse>,
                response: retrofit2.Response<TokenResponse>
            ) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    Log.e("MainActivity", "Token: $token")
                    when (response.code()) {
                        201 -> {
                            token?.let { starVoveSession(it) }
                        }
                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.printStackTrace()}")
            }
        })
    }

    private fun launchVerificationActivity() {
        //generate uuid
        val uuid = java.util.UUID.randomUUID().toString()

        //Get token
//        viewModel.verificationToken(uuid).observe(this) { result ->
//            if (result.isSuccessful) {
//                when (result.code()) {
//                    200 -> {
//                        result.body()?.let { starVoveSession(it.token) }
//                    }
//                }
//            }
//        }
    }

    private fun starVoveSession(token: String) {
        //set locale
        Vove.setLocale(this, VoveLocale.AR_MA)
        Vove.setEnableVocalGuidance(true) // Enable vocal guidance

        Vove.processIDMatching(this, VoveEnvironment.SANDBOX, token) { verificationResult ->
            CoroutineScope(Dispatchers.Main).launch {
                Log.e("Verification", verificationResult.toString())
                when (verificationResult) {
                    SUCCESS -> Toast.makeText(
                        this@MainActivity,
                        "Verification success",
                        Toast.LENGTH_LONG
                    )
                        .show()

                    FAILURE -> Toast.makeText(
                        this@MainActivity,
                        "Verification failed",
                        Toast.LENGTH_LONG
                    ).show()

                    PENDING -> Toast.makeText(
                        this@MainActivity,
                        "Verification pending",
                        Toast.LENGTH_LONG
                    )
                        .show()

                    else -> {}
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CODE_PERMISSION -> if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                Log.e("", "Please grant : $permissions ${hasPermissions(this, PERMISSIONS)}")
        }
    }


    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

}

interface VerificationService {


    @POST("sessions")
    fun verificationToken(@Body tokenRequest: TokenRequest): Call<TokenResponse>
}

@Serializable
data class TokenResponse(
    val token: String
)

@Serializable
data class TokenRequest(
    val refId: String
)
