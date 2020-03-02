package com.mslxl.fubuki_tsuhatsuha.data

import com.mslxl.fubuki_tsuhatsuha.data.model.UserInfo
import com.mslxl.fubuki_tsuhatsuha.data.model.WorkList

class QueryRepository {
    private val dataSource = WebDataSource


    fun getUserInfo(token: String): Result<UserInfo> {
        return dataSource.getUserInfo(token)
    }

    fun getWorkList(ru: String, token: String): Result<WorkList> {
        return dataSource.getWorkList(ru, token)
    }
}