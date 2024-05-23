package com.chari.money

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface VerificationService {
    @POST("sessions")
   suspend fun verificationToken(@Body tokenRequest: TokenRequest): Response<TokenResponse>
}
