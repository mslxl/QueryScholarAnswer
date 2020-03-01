package com.mslxl.fubuki_tsuhatsuha.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mslxl.fubuki_tsuhatsuha.data.model.User

@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}