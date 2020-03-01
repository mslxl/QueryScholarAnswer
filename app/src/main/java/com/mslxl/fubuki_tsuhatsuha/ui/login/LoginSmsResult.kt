package com.mslxl.fubuki_tsuhatsuha.ui.login

data class LoginSmsResult(
    val successToken: String? = null,
    val errorMsg: String? = null,
    val error: Int? = null
)