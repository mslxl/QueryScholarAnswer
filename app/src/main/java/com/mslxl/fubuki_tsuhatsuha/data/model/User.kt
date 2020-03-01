package com.mslxl.fubuki_tsuhatsuha.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Entity
data class User(
    @ColumnInfo val token: String,
    @PrimaryKey val id: Int = 1
)
