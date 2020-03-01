package com.mslxl.fubuki_tsuhatsuha.ui.query

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mslxl.fubuki_tsuhatsuha.data.QueryRepository
import kotlin.concurrent.thread

class QueryViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: QueryRepository
) : ViewModel() {
    val token: String = savedStateHandle["token"]!!
    private val _requestUserInfoResult = MutableLiveData<RequestUserInfoResult>()
    val requestUserInfoResult: LiveData<RequestUserInfoResult> = _requestUserInfoResult

    private val _requestWorkListResult = MutableLiveData<RequestWorkListResult>()
    val requestWorkListResult: LiveData<RequestWorkListResult> = _requestWorkListResult

    init {
        Log.d("webdata", token)
    }


    fun requestUserInfo() {
        thread(name = "Request info") {
            val result = repository.getUserInfo(token)
            _requestUserInfoResult.postValue(result)
        }
    }

    fun requestWork() {
        thread(name = "Request work") {
            val result = repository.getWorkList(requestUserInfoResult.value!!.success!!.ru, token)
            _requestWorkListResult.postValue(result)
        }

    }
}