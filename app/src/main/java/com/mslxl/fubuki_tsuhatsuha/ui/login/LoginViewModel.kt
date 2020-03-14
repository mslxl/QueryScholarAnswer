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

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    private val _smsCountdown = MutableLiveData<Long>().apply {
        value = 0
    }
    val smsCountdownState: LiveData<Long> = _smsCountdown

    // Token
    private val _smsResult = MutableLiveData<Result<String>>()
    val smsResult: LiveData<Result<String>> = _smsResult


    private val _useVerifyCode = MutableLiveData(loginRepository.useVerifyCode)
    var useVerifyCode: LiveData<Boolean> = _useVerifyCode.apply {
        observeForever {
            loginRepository.useVerifyCode = it
        }
    }


    val savedPhone = loginRepository.savedPhone?:""

    val savedPassword = loginRepository.savedPassword?:""


    var isSavePasswordEnable:Boolean
        set(value) {
            loginRepository.isSavePasswordEnable = value
        }
        get() = loginRepository.isSavePasswordEnable


    fun updateLocalData() {
        loginRepository.getLoggedInUser()?.let {
            _loginResult.value = Result.Success(it)
        }
    }

    fun useVerifyCode(use: Boolean) {
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
                if (smsResult.value == null) {
                    Result.Error(status = -1, message = "无 SMS 信息")
                } else {
                    if (smsResult.value is Result.Success) {

                        // 此处编译器有 bug
                        // 直接访问会 Unresolved reference 或被判定为多条语句
                        // val v3 = (smsResult.value!! as Result.Success).data
                        val v:Result<String>? = smsResult.value
                        val v1 = v!!
                        val v2 = v1 as Result.Success
                        val v3 = v2.data


                        loginRepository.login(phone, v3, verifyCode)

                    } else {
                        Result.Error(status = -1, message = "SMS 信息错误")
                    }
                }
            }

            _loginResult.postValue(result)
        }
    }

    fun savePhone(phone: String){
        loginRepository.savedPhone = phone
    }
    fun savePassword(pwd:String){
        loginRepository.savedPassword = pwd
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
