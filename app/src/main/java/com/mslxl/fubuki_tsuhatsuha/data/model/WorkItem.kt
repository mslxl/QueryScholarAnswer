package com.mslxl.fubuki_tsuhatsuha.data.model

data class WorkItem(
    val name: String,
    val homeWorkGuid: String,
    val endReleaseTime: String,
    val subject: String,
    val state: Int, //0未完成,4完成
    val days: Int,
    val hours: Int,
    val isOverdue: Boolean
)