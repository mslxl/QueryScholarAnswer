package com.mslxl.fubuki_tsuhatsuha.data

import com.mslxl.fubuki_tsuhatsuha.ui.query.RequestUserInfoResult
import com.mslxl.fubuki_tsuhatsuha.ui.query.RequestWorkListResult

class QueryRepository {
    private val dataSource = WebDataSource


    fun getUserInfo(token: String): RequestUserInfoResult {
        return dataSource.getUserInfo(token)
    }

    fun getWorkList(ru: String, token: String): RequestWorkListResult {
        return dataSource.getWorkList(ru, token)
    }
}