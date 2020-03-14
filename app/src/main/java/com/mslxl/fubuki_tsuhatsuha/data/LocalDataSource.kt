package com.mslxl.fubuki_tsuhatsuha.data

import android.content.SharedPreferences

class LocalDataSource(private val preferences: SharedPreferences) {
    private companion object {
        const val TOKEN = "token"
        const val PHONE = "phone"
        const val PASSWORD = "pwd"
        const val SAVE_PASSWORD = "savePwd"
        const val USE_VERIFY_CODE = "verifyCode"
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
    var password:String? = preferences.getString(PASSWORD,null)
        set(value) {
            preferences.edit().let {
                it.putString(PASSWORD, value)
                it.commit()
            }
            field = value
        }
    var savePwd:Boolean = preferences.getBoolean(SAVE_PASSWORD,false)
        set(value) {
            preferences.edit().let {
                it.putBoolean(SAVE_PASSWORD, value)
                it.commit()
            }
            field = value
        }
    var useVerifyCode:Boolean = preferences.getBoolean(USE_VERIFY_CODE,false)
        set(value) {
            preferences.edit().let {
                it.putBoolean(USE_VERIFY_CODE, value)
                it.commit()
            }
            field = value
        }
}