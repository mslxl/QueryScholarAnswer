package com.mslxl.fubuki_tsuhatsuha.ui.query

import com.mslxl.fubuki_tsuhatsuha.data.model.UserInfo

data class RequestUserInfoResult(
    val success: UserInfo? = null,
    val errorCode: Int? = null,
    val msg: String? = null
)