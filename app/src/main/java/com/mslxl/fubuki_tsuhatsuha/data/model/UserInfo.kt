package com.mslxl.fubuki_tsuhatsuha.data.model

data class UserInfo(
    val username: String,
    val cityCode: String,
    val ru: String,
    val schoolGuid: String,
    val schoolName: String,
    val gradeCode: String,
    val classCode: String,
    val className: String,
    val homework: Boolean
)