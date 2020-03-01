package com.mslxl.fubuki_tsuhatsuha.data

import android.util.Log
import com.mslxl.fubuki_tsuhatsuha.ui.login.AllowStartResult
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object SoftwareControl {
    // 留后门禁止启动
    // 一定程度上防同学
    fun allowStart(): AllowStartResult {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://blog.mslxl.com/software_config.json")
            .get()
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body!!.string()
        val json = JSONObject(responseBody).getJSONObject("queryScholarAnswer")
        Log.d("webdata", responseBody)
        return AllowStartResult(json.getBoolean("allowStart"), json.getString("disableMsg"))
    }
}