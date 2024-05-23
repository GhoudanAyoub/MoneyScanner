package com.chari.money

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import retrofit2.Response

@HiltViewModel
class RemoteViewModel @Inject constructor(
    private val verificationService: VerificationService
) : ViewModel() {

//    fun verificationToken(uuid: String): LiveData<Response<TokenResponse>> {
//        return liveData {
//            verificationService.verificationToken(TokenRequest(uuid))
//                .collect { result ->
//                    emit(result)
//                }
//        }
//    }
}
