package com.mslxl.fubuki_tsuhatsuha.data

class AnswerRepository {
    private val dataSource = WebDataSource

    fun requestAnswer(token: String, ru: String, guid: String) =
        dataSource.getAnswer(ru = ru, workGuid = guid, token = token)
}