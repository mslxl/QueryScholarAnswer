package com.mslxl.fubuki_tsuhatsuha.ui.answer

import com.mslxl.fubuki_tsuhatsuha.data.model.Answer

data class RequestAnswerResult(
    val success: Answer? = null,
    val status: Int? = 0,
    val msg: String? = null
)