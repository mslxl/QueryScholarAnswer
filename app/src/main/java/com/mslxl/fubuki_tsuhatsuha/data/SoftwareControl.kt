package com.mslxl.fubuki_tsuhatsuha.data

import android.util.Log
import com.mslxl.fubuki_tsuhatsuha.data.util.buildOkHttp
import com.mslxl.fubuki_tsuhatsuha.ui.login.AllowStartResult
import org.json.JSONObject

object SoftwareControl {
    // 留后门禁止启动
    // 一定程度上防同学
    fun allowStart(): AllowStartResult {
        return buildOkHttp<AllowStartResult> {
            url("https://blog.mslxl.com/software_config.json")
            get()
        }.onError {
            return@onError AllowStartResult(true, "无网络连接")
        }.onSuccess {
            val json = JSONObject(it).getJSONObject("queryScholarAnswer")
            Log.d("control", it)
            return@onSuccess AllowStartResult(
                json.getBoolean("allowStart"),
                json.getString("disableMsg")
            )
        }.exec()
    }
}