package com.chari.money

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.activity.viewModels
import com.indatacore.skyAnalytics.skyID.DocumentAnalyzer
import com.indatacore.skyAnalytics.skyID.FacebasedAuthenticator
import com.indatacore.skyAnalytics.skyID.tools.Country
import com.indatacore.skyAnalytics.skyID.tools.Language
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit

@AndroidEntryPoint
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


    val skyIdCertificate = "-----BEGIN CERTIFICATE-----\n" +
            "MIIG/jCCBOagAwIBAgIRAO0ZRtGvJnE2IWuQgPeeg54wDQYJKoZIhvcNAQEMBQAw\n" +
            "SzELMAkGA1UEBhMCQVQxEDAOBgNVBAoTB1plcm9TU0wxKjAoBgNVBAMTIVplcm9T\n" +
            "U0wgUlNBIERvbWFpbiBTZWN1cmUgU2l0ZSBDQTAeFw0yNDAxMDgwMDAwMDBaFw0y\n" +
            "NTAxMDcyMzU5NTlaMCUxIzAhBgNVBAMTGmRlbW8uc2t5aWRlbnRpZmljYXRpb24u\n" +
            "Y29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAngTP+k6E/KtA5xtk\n" +
            "PJznlYnTGDTJYGs1tjDd6XI9SyF4YrDmQwWkRI7sgtwgHDYPqVgfGUn/+UGtJW+V\n" +
            "Z21u9XK8Cd+CmgmwyCCsCePFqs6403vjru9Q8TWR/YVtujJP51ygPiAEareB9dXb\n" +
            "kAR8GuzNZiWdGAQEXVGox5iXWTeFn5rHP0IFnF6xcSVlUinRNgpisRq19Q/gSCje\n" +
            "fGCPiNL+FXret5F/lCFOsTwwsqo/aPAPEXa1c0BBkitx/mxub95HHojs1aOZAU78\n" +
            "XwCxbY4Zeqrwdi4YI6TC0aUhrMi+tS97xHDAiL3sQ3Fxyqzh30moDFeye/hzDf6W\n" +
            "mdEn1QIDAQABo4IDATCCAv0wHwYDVR0jBBgwFoAUyNl4aKLZGWjVPXLeXwo+3LWG\n" +
            "hqYwHQYDVR0OBBYEFJGq6Gr3VlufQ9r80l36WNWz2UxuMA4GA1UdDwEB/wQEAwIF\n" +
            "oDAMBgNVHRMBAf8EAjAAMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjBJ\n" +
            "BgNVHSAEQjBAMDQGCysGAQQBsjEBAgJOMCUwIwYIKwYBBQUHAgEWF2h0dHBzOi8v\n" +
            "c2VjdGlnby5jb20vQ1BTMAgGBmeBDAECATCBiAYIKwYBBQUHAQEEfDB6MEsGCCsG\n" +
            "AQUFBzAChj9odHRwOi8vemVyb3NzbC5jcnQuc2VjdGlnby5jb20vWmVyb1NTTFJT\n" +
            "QURvbWFpblNlY3VyZVNpdGVDQS5jcnQwKwYIKwYBBQUHMAGGH2h0dHA6Ly96ZXJv\n" +
            "c3NsLm9jc3Auc2VjdGlnby5jb20wJQYDVR0RBB4wHIIaZGVtby5za3lpZGVudGlm\n" +
            "aWNhdGlvbi5jb20wggF/BgorBgEEAdZ5AgQCBIIBbwSCAWsBaQB2AM8RVu7VLnyv\n" +
            "84db2Wkum+kacWdKsBfsrAHSW3fOzDsIAAABjOjEBlUAAAQDAEcwRQIgYHSt2yBp\n" +
            "p0As9eE9UWDqXiEwW26eufBshKHKK9Wq15cCIQCTj+4T/BD1dcjqM91dlcTYRMYY\n" +
            "714B4RJn84Njjm3cMQB2AKLjCuRF772tm3447Udnd1PXgluElNcrXhssxLlQpEfn\n" +
            "AAABjOjEBksAAAQDAEcwRQIgJeQL4/4WNYL5OAJDGxUJ+H/6osbHYYKpgO0Pr0Qd\n" +
            "9vMCIQCq2u0+6knR5NLhL7L6uXWsSxuI5v/BSD1rPg/AuMgrxQB3AE51oydcmhDD\n" +
            "OFts1N8/Uusd8OCOG41pwLH6ZLFimjnfAAABjOjEBjAAAAQDAEgwRgIhALapDVp5\n" +
            "6doqpOCUMRAX/UEXRfOVaQiS69w8HxB5OR7UAiEAhw9E6CtWTQxAfSuGve3BgXWv\n" +
            "uAU4Cj+q4zDuze/BDnUwDQYJKoZIhvcNAQEMBQADggIBAElgiD1wpzcGoy5N8YsV\n" +
            "FvcH57TKnOnunYV0DjGNauCJppwYMSqyuB7CNfeDGbOg2BZVwGC8M7XzojbueNKE\n" +
            "bCggr0BG3H3hdW4rTQq46qcuK6rPDfqAAo3YQ2JROlvX1Y91+i1KizFFHon7RKBa\n" +
            "Kr3ks+pmT8yIx+7kj5tcC4PmUDx0cK/ZvEd0HZkoIoyw7EbBRI78Z2MiTyB8s/8A\n" +
            "UCP3jrEjCiAVg4GuOpJ26vVsQwhClJp3J2qMCwTLOCOfHbM6IjjyKalaj7ozb7Jg\n" +
            "bHRusf1UfLOm2ZvM4sDfGmsn1kmRg0B0WnSKpMoOhW0oQpGhHdPNj0k4DNhsKOT6\n" +
            "MtGfDpm1LG9FKn8akg2QdxUeyEPakm40pNl+seIE/cZjYuB6km2pLjt3wCzTMTEr\n" +
            "FIPuJrY3affqEW9fdhIpNMqXsnmdKu5AleKizdILlvDgnjOjzJl802lW+urwGlqU\n" +
            "kIaBnMJvNIlw1Bu9oWcwfrNyjCwp3GWuqJSyRHXKsXbCNkUZCmdSr2oOw5yjd2wK\n" +
            "Fla2eeGFQVEMm/gMtUmrSrRaZ2CkmKtDz7fIGsHnUtR+Xk3qeuu7QyyLh/pSPFeY\n" +
            "dI4p90lccJHlTMjpUd92p2q5GJdZzrUDHaa2/6a1wFZHNlzXYvbYMUQnbSGRyslT\n" +
            "fB2iUhAVAjUV73L7g0fRERKe\n" +
            "-----END CERTIFICATE-----\n"
    var Token: String = "099363837274245938196079185454775"
    val serviceSettings = JSONObject()
    var document: Boolean = true

    val doc1 = mutableListOf<String>()
    private val viewModel by viewModels<RemoteViewModel>()

    @Inject
    lateinit var httpClient: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView_label = findViewById(R.id.textView_label)
        textView_1 = findViewById(R.id.textView_1)
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)

        try {
            serviceSettings.put(
                "get_parameters_url",
                "https://demo.skyidentification.com:7009/get_parameters"
            )
            serviceSettings.put(
                "certificate@demo.skyidentification.com",
                skyIdCertificate
            )
            serviceSettings.put("crop_faces", true)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        val btn: Button = findViewById(R.id.button_DocOcerization)
        btn.setOnClickListener { launchDocOcerizationActivity() }


        val btn2: Button = findViewById(R.id.button_feceRec)
        btn2.setOnClickListener { launchFaceActivity() }

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

    fun clearTextViews() {
        textView_label?.text = ""
        textView_1?.text = ""
    }


    fun launchDocOcerizationActivity() {
        document = true
        if (hasPermissions(this, PERMISSIONS)) {
            clearTextViews()
            val intent = Intent(this, DocumentAnalyzer::class.java)
            startScanningActivity(
                intent,
                DocumentAnalyzer.RequestCode,
                Language.FRENCH,
                Country.MOROCCO,
                "0102"
            )
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }
    }

    fun launchFaceActivity() {
        document = false
        if (hasPermissions(this, PERMISSIONS)) {
            clearTextViews()

            val intent = Intent(this, FacebasedAuthenticator::class.java)
            startScanningActivity(
                intent,
                FacebasedAuthenticator.RequestCode,
                Language.FRENCH,
                Country.MOROCCO,
                ""
            )
        } else ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
    }

    private val faceAuthenticatorLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.e("", "FaceAuthenticationResult: ${result}")
        if (result.resultCode == FacebasedAuthenticator.RESULT_OK) {

            val StatusCode: String = result.data?.getStringExtra("StatusCode") ?: ""
            val StatusLabel: String = result.data?.getStringExtra("StatusLabel") ?: ""
            val sky_face_match_decision_code: String =
                result.data?.getStringExtra("sky_face_match_decision_code") ?: ""
            val sky_face_match_decision_label: String =
                result.data?.getStringExtra("sky_face_match_decision_label") ?: ""
            val sky_face_match_decision_codes_per_document: Map<String, String> =
                result.data?.getSerializableExtra("sky_face_match_decision_codes_per_document") as Map<String, String>


            textView_label?.text =
                "FaceAuthenticatorLauncher: ${sky_face_match_decision_label} ${sky_face_match_decision_code} " +
                        "\n ${sky_face_match_decision_codes_per_document}"

            Log.e("", textView_label?.text.toString())
        } else if (result.resultCode == DocumentAnalyzer.RESULT_Not_OK) {
            val StatusCode: String = result.data?.getStringExtra("StatusCode") ?: ""
            val StatusLabel: String = result.data?.getStringExtra("StatusLabel") ?: ""
            textView_label?.text = "FaceAuthenticatorLauncher: ${StatusCode} ${StatusLabel}"
        }

    }

    private val documentAnalyzerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.e("", "DocumentAnalyzerResult: ${result}")
        if (result.resultCode == DocumentAnalyzer.RESULT_OK) {

            val StatusCode: String = result.data?.getStringExtra("StatusCode") ?: ""
            val StatusLabel: String = result.data?.getStringExtra("StatusLabel") ?: ""
            val RequestedInformations: String =
                result.data?.getStringExtra("RequestedInformations") ?: ""

            val Json: Json = Json {
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = false
            }

            val RequestedFiles = try {
                Json.decodeFromString(
                    RequestedFile.serializer(), result.data?.getStringExtra("RequestedFiles") ?: ""
                )
            } catch (e: Exception) {
                null
            }

            RequestedFiles?.FaceFile_1?.let { doc1.add(it) }
            RequestedFiles?.FaceFile_2?.let { doc1.add(it) }


            textView_label?.text =
                "DocumentAnalyzerResult: ${RequestedInformations} \n ${RequestedFiles}"
        } else if (result.resultCode == DocumentAnalyzer.RESULT_Not_OK) {
            textView_label?.text = "DocumentAnalyzerResult: ${result.data}"
        }

    }

    fun startScanningActivity(
        intent: Intent,
        requestCode: Int,
        language: Language,
        country: Country,
        serviceID: String
    ) {
        if (document) {
            intent.putExtra("requestCode", requestCode)
            intent.putExtra("Language", language.toString())
            intent.putExtra("Country", country.toString())
            intent.putExtra("ServiceID", serviceID)
            intent.putExtra("ServiceSettings", serviceSettings.toString())
            intent.putExtra("Token", Token)
            documentAnalyzerLauncher.launch(intent)
        } else {
            if (doc1.isNotEmpty()) {

                val documentFiles = hashMapOf<String, MutableList<String>>()
                documentFiles["doc_1"] = doc1

                intent.putExtra("DocumentFiles", documentFiles)
                intent.putExtra("RequestCode", requestCode)
                intent.putExtra("Language", language.iso)
                intent.putExtra("ServiceSettings", serviceSettings.toString())
                intent.putExtra("Token", Token)
                faceAuthenticatorLauncher.launch(intent)
            } else {
                Toast.makeText(this, "Please scan the document first", Toast.LENGTH_LONG).show()
                Log.e("TAG", "startScanningActivity: EmptyFile")
            }

        }
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


@Serializable
data class RequestedFile(
    val DocumentFrontSideFile: String,
    val FaceFile_1: String,
    val FaceFile_2: String
)
