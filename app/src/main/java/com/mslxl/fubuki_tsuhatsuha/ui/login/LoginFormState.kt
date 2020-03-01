package com.mslxl.fubuki_tsuhatsuha.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isPhoneValid: Boolean = false,
    val isDataValid: Boolean = false
)
