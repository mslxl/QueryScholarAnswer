package com.mslxl.fubuki_tsuhatsuha.data


import androidx.lifecycle.LiveData
import com.mslxl.fubuki_tsuhatsuha.data.model.User

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(
    private val localDataSource: LocalDataSource
) {
    private val dataSource = WebDataSource

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore

    }
    var isSavePasswordEnable:Boolean
        set(value) {
            localDataSource.savePwd = value
        }
        get() = localDataSource.savePwd

    fun isAllowStart() = SoftwareControl.allowStart()


    // Return sms token
    fun sendSms(phone: String): Result<String> {
        return dataSource.sendSms(phone)
    }


    fun getLoggedInUser():User? {
        return localDataSource.token?.let { User(it) }
    }

    fun getSavedPhone():String? = localDataSource.phone


    fun savePhone(string: String){
        localDataSource.phone = string
    }


    fun getSavedPassword():String? = localDataSource.password

    fun savePassword(pwd:String){
        localDataSource.password = pwd
    }



    fun login(phone: String, token: String, verifyCode: String): Result<User> {
        val result = dataSource.login(phone, token, verifyCode)
        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }
        return result
    }

    fun login(phone: String, password: String): Result<User> {
        val result = dataSource.login(phone, password)
        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }
        return result
    }

    private fun setLoggedInUser(loggedInUser: User) {
       localDataSource.token = loggedInUser.token
    }
}
