package com.mslxl.fubuki_tsuhatsuha.data.model


data class Answer(
    val guid: String,
    val choice: List<String>,
    val subAnswer: List<String>
)