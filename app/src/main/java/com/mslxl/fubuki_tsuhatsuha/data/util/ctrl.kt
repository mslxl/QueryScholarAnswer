package com.mslxl.fubuki_tsuhatsuha.data.util

import android.util.Log
import org.json.JSONObject
import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun checkAllowStart() {
    thread {
        buildOkHttp<Unit> {
            url("https://blog.mslxl.com/software_config.json")
            get()
        }.onError {

        }.onSuccess {
            val json = JSONObject(it).getJSONObject("queryScholarAnswer")
            Log.d("control", it)
            if (json.getBoolean("allowStart2").not()) {
                exitProcess(-1)
            }
        }.exec()
    }
}