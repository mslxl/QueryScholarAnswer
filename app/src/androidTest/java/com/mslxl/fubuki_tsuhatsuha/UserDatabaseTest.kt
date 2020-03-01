package com.mslxl.fubuki_tsuhatsuha

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mslxl.fubuki_tsuhatsuha.data.db.UserDatabase
import com.mslxl.fubuki_tsuhatsuha.data.model.User
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDatabaseTest {
    private val context: Context by lazy {
        InstrumentationRegistry.getInstrumentation().targetContext
    }
    private lateinit var db: UserDatabase
    private val userDataForTest = User("konkonkitsune", (Math.random() * 10).toInt())
    @Before
    fun load() {
        db = Room.databaseBuilder(context, UserDatabase::class.java, "user-test.db").build()
    }

    @Test
    fun save() {
        val dao = db.userDao()
        dao.save(userDataForTest)
    }

    @After
    fun check() {

        val dao = db.userDao()
        dao.loadAll().map { it.toString() }.forEach { Log.d("userDataTest", it) }
        val u = dao.load(userDataForTest.id)
        assertEquals(userDataForTest, u)


    }


}