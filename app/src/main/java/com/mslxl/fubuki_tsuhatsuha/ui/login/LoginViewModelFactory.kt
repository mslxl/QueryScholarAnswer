package com.mslxl.fubuki_tsuhatsuha.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.mslxl.fubuki_tsuhatsuha.data.LoginRepository
import com.mslxl.fubuki_tsuhatsuha.data.db.UserDatabase

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
                    userDatabase = Room.databaseBuilder(
                        applicationContext,
                        UserDatabase::class.java,
                        "user.db"
                    ).build()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
