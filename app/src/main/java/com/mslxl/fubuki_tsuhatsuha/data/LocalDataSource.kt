package com.mslxl.fubuki_tsuhatsuha.data

import android.content.SharedPreferences

class LocalDataSource(private val preferences: SharedPreferences) {
    private companion object {
        const val TOKEN = "token"
        const val PHONE = "phone"
    }

    var token:String? = preferences.getString(TOKEN, null)
       set(value) {
           preferences.edit().let {
               it.putString(TOKEN, value)
               it.commit()
           }
           field = value
       }
    var phone:String? = preferences.getString(PHONE, null)
        set(value) {
            preferences.edit().let {
                it.putString(PHONE, value)
                it.commit()
            }
            field = value
        }



}