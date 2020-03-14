package com.mslxl.fubuki_tsuhatsuha.data


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

    var savedPhone:String?
        set(value) {
            localDataSource.phone = value
        }
        get() = localDataSource.phone

    var savedPassword:String?
        set(value) {
            localDataSource.password = value
        }
        get() = localDataSource.password

    var useVerifyCode:Boolean
        set(value) {
            localDataSource.useVerifyCode = value
        }
        get() = localDataSource.useVerifyCode


    // Return sms token
    fun sendSms(phone: String): Result<String> {
        return dataSource.sendSms(phone)
    }


    fun getLoggedInUser():User? {
        return localDataSource.token?.let { User(it) }
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
