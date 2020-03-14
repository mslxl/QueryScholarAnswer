package com.mslxl.fubuki_tsuhatsuha.data

import com.mslxl.fubuki_tsuhatsuha.data.model.UserInfo
import com.mslxl.fubuki_tsuhatsuha.data.model.WorkList

class QueryRepository(private val localDataSource: LocalDataSource) {
    private val dataSource = WebDataSource

    var secondPassword
        set(value) {
            localDataSource.secondPassword = value
        }
        get() = localDataSource.secondPassword

    fun getUserInfo(token: String): Result<UserInfo> {
        return dataSource.getUserInfo(token)
    }

    fun getWorkList(ru: String, token: String): Result<WorkList> {
        return dataSource.getWorkList(ru, token)
    }
}