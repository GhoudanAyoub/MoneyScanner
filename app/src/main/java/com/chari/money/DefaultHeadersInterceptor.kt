package com.chari.money

import dagger.Reusable
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

@Reusable
class DefaultHeadersInterceptor @Inject constructor(
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .header(
                "x-api-key",
                "27de73cda76c23a5739c79b52383bdab0d434fea0f0fa3b568730a24ed61cf33"
            )
            .build()

        return chain.proceed(request)
    }
}
