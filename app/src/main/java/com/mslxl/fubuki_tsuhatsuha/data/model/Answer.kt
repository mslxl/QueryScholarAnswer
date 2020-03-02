package com.mslxl.fubuki_tsuhatsuha.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Answer(
    @PrimaryKey val guid: String,
    @ColumnInfo val choice: List<String>,
    @ColumnInfo val subAnswer: List<String>
)