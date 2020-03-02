package com.mslxl.fubuki_tsuhatsuha.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.mslxl.fubuki_tsuhatsuha.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = REPLACE)
    fun save(user: User)

    @Query("SELECT * FROM user WHERE id = :userId")
    fun loadLiveData(userId: Int): LiveData<User?>

    @Query("SELECT * FROM user WHERE id = :userId")
    fun load(userId: Int): User?

    @Query("SELECT * FROM user")
    fun loadAll(): List<User>

    @Delete(entity = User::class)
    fun delete(user: User)
}