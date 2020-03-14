package com.mslxl.fubuki_tsuhatsuha.ui.login

import android.os.CountDownTimer
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mslxl.fubuki_tsuhatsuha.R
import com.mslxl.fubuki_tsuhatsuha.data.LoginRepository
import com.mslxl.fubuki_tsuhatsuha.data.Result
import com.mslxl.fubuki_tsuhatsuha.data.model.User
import kotlin.concurrent.thread

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _smsCountdown = MutableLiveData<Long>().apply {
        value = 0
    }
    val smsCountdownState: LiveData<Long> = _smsCountdown

    // Token
    private val _smsResult = MutableLiveData<Result<String>>()
    val smsResult: LiveData<Result<String>> = _smsResult

    private val _allowStart = MutableLiveData<AllowStartResult>()
    val allowStart: LiveData<AllowStartResult> = _allowStart

    val loggedInUser: LiveData<User?> = loginRepository.readLoggedInUserInDatabase()

    private val _useVerifyCode = MutableLiveData<Boolean>()
    var useVerifyCode: LiveData<Boolean> = _useVerifyCode


    fun isAllowStart() {
        thread(name = "allow start") {
            val result = loginRepository.isAllowStart()
            _allowStart.postValue(result)
        }
    }

    fun setUseVerifyCode(use: Boolean) {
        _useVerifyCode.value = use
    }

    fun sendSMS(phone: String) {
        thread(name = "sms") {
            // Countdown to avoid send sms duplicated
            Looper.prepare()
            val timer = object : CountDownTimer(60 * 1000, 1000) {
                override fun onFinish() {
                    _smsCountdown.postValue(0)
                    _smsCountdown.value = 0
                }

                override fun onTick(p0: Long) {
                    _smsCountdown.postValue(p0)
                }
            }
            timer.start()

            val result = loginRepository.sendSms(phone)
            _smsResult.postValue(result)
        }
    }

    fun login(phone: String, verifyCode: String) {
        // can be launched in a separate asynchronous job

            thread(name = "login") {
                val result = if (!useVerifyCode.value!!) {
                    loginRepository.login(phone, verifyCode)
                } else {
                    smsResult.value?.let {
                        return@let if (it is Result.Success) {
                            loginRepository.login(phone, it.data, verifyCode)
                        } else {
                            Result.Error(-1, "")
                        }
                    }
                }

                if (result is Result.Success) {
                    _loginResult.postValue(LoginResult(success = LoggedInUserView(result.data.token)))
                } else {
                    _loginResult.postValue(LoginResult(error = R.string.login_failed))
                }
            }


    }

    fun loginDataChanged(username: String) {
        if (!isPhoneValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else {
            _loginForm.value = LoginFormState(isPhoneValid = true, isDataValid = true)
        }

    }

    // A placeholder phone validation check
    private fun isPhoneValid(phone: String): Boolean {
        return phone.length == 11 && phone.toLongOrNull() != null
    }


}
