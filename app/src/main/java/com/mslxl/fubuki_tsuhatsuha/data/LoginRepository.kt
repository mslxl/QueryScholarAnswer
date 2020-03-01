package com.mslxl.fubuki_tsuhatsuha.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.mslxl.fubuki_tsuhatsuha.data.db.UserDatabase
import com.mslxl.fubuki_tsuhatsuha.data.model.User

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(
    private val userDatabase: UserDatabase
) {
    private val dataSource = WebDataSource
    private val userDao by lazy {
        userDatabase.userDao()
    }

    // in-memory cache of the loggedInUser object
    var user: LiveData<User?>
        private set

    val isLoggedIn: Boolean
        get() = user.value != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = userDao.loadLiveData(1)!!
        Log.v("load user", user.value?.token ?: "No login user")
    }

    fun logout() {
        user.value?.let {
            userDao.delete(it)
        }
    }

    fun isAllowStart() = SoftwareControl.allowStart()


    // Return sms token
    fun sendSms(phone: String): Result<String> {
        return dataSource.sendSms(phone)
    }


    fun readLoggedInUserInDatabase(): LiveData<User?>? {
        return userDao.loadLiveData(1)
    }

    fun login(phone: String, token: String, verifyCode: String): Result<User> {
        val result = dataSource.login(phone, token, verifyCode)
        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }
        return result
    }

    private fun setLoggedInUser(loggedInUser: User) {
        Log.i("login", user.toString())
        userDao.save(loggedInUser)
    }
}
