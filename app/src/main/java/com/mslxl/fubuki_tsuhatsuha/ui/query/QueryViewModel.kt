package com.mslxl.fubuki_tsuhatsuha.ui.query

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mslxl.fubuki_tsuhatsuha.data.QueryRepository
import com.mslxl.fubuki_tsuhatsuha.data.Result
import com.mslxl.fubuki_tsuhatsuha.data.model.UserInfo
import com.mslxl.fubuki_tsuhatsuha.data.model.WorkList
import kotlin.concurrent.thread

class QueryViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: QueryRepository
) : ViewModel() {
    val token: String = savedStateHandle["token"]!!
    private val _requestUserInfoResult = MutableLiveData<Result<UserInfo>>()
    val requestUserInfoResult: LiveData<Result<UserInfo>> = _requestUserInfoResult

    private val _requestWorkListResult = MutableLiveData<Result<WorkList>>()
    val requestWorkListResult: LiveData<Result<WorkList>> = _requestWorkListResult

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
            val result =
                if (requestUserInfoResult.value != null && requestUserInfoResult.value is Result.Success) {
                    repository.getWorkList(
                        (requestUserInfoResult.value as Result.Success<UserInfo>).data.ru,
                        token
                    )
                } else {
                    Result.Error(status = -1, message = "用户信息获取失败")
                }
            _requestWorkListResult.postValue(result)
        }

    }
}