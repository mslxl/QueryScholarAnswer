package com.mslxl.fubuki_tsuhatsuha.ui.answer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mslxl.fubuki_tsuhatsuha.data.AnswerRepository
import kotlin.concurrent.thread

class AnswerViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: AnswerRepository
) : ViewModel() {
    val token: String = savedStateHandle["token"]!!
    val guid: String = savedStateHandle["guid"]!!
    val ru: String = savedStateHandle["ru"]!!

    private val _requestAnswerResult = MutableLiveData<RequestAnswerResult>()
    val requestAnswerResult: LiveData<RequestAnswerResult> = _requestAnswerResult

    fun requestAnswer() {
        thread(name = "Request answer:${guid}") {
            val result = repository.requestAnswer(token, ru, guid)
            _requestAnswerResult.postValue(result)
        }
    }
}