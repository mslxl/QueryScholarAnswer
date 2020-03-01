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

    private val _smsResult = MutableLiveData<LoginSmsResult>()
    val smsResult: LiveData<LoginSmsResult> = _smsResult

    private val _allowStart = MutableLiveData<AllowStartResult>()
    val allowStart: LiveData<AllowStartResult> = _allowStart

    val loggedInUser: LiveData<User?>? = loginRepository.readLoggedInUserInDatabase()

    fun isAllowStart() {
        thread(name = "allow start") {
            val result = loginRepository.isAllowStart()
            _allowStart.postValue(result)
        }
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
            if (result is Result.Success) {
                _smsResult.postValue(LoginSmsResult(successToken = result.data))
            } else if (result is Result.Error) {
                _smsResult.postValue(LoginSmsResult(errorMsg = result.exception.message))
            }
        }
    }

    fun login(phone: String, verifyCode: String) {
        // can be launched in a separate asynchronous job
        smsResult.value?.successToken?.let { token ->
            thread(name = "login") {
                val result = loginRepository.login(phone, token, verifyCode)

                if (result is Result.Success) {
                    _loginResult.postValue(LoginResult(success = LoggedInUserView(result.data.token)))
                } else {
                    _loginResult.postValue(LoginResult(error = R.string.login_failed))
                }
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isPhoneValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else {
            if (!isPasswordValid(password)) {
                _loginForm.value =
                    LoginFormState(isPhoneValid = true, passwordError = R.string.invalid_password)
            } else {
                _loginForm.value = LoginFormState(isDataValid = true, isPhoneValid = true)
            }
        }
    }

    // A placeholder phone validation check
    private fun isPhoneValid(phone: String): Boolean {
        return phone.length == 11 && phone.toLongOrNull() != null
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 4
    }

}
