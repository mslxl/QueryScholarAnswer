package com.mslxl.fubuki_tsuhatsuha.ui.login

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mslxl.fubuki_tsuhatsuha.data.LocalDataSource
import com.mslxl.fubuki_tsuhatsuha.data.LoginRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
*/
class LoginViewModelFactory(private val applicationContext: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginRepository = LoginRepository(
                    localDataSource= LocalDataSource(applicationContext.getSharedPreferences("data",Context.MODE_PRIVATE))
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
