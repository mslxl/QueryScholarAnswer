package com.mslxl.fubuki_tsuhatsuha.ui.query

import com.mslxl.fubuki_tsuhatsuha.data.model.WorkList

data class RequestWorkListResult(
    val success: WorkList? = null,
    val code: Int? = null,
    val msg: String? = null
)