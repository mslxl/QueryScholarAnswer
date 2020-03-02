package com.mslxl.fubuki_tsuhatsuha.data.util

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody

class OkHttpWarp<R>(private val request: Request) {
    private lateinit var _onerror: ((Exception) -> R)
    private lateinit var _parser: ((String) -> R)


    var body: ResponseBody? = null
        private set

    val bodyString by lazy {
        body!!.string()
    }

    fun onError(onerror: (Exception) -> R): OkHttpWarp<R> {
        _onerror = onerror
        return this
    }


    fun onSuccess(parser: (body: String) -> R): OkHttpWarp<R> {
        _parser = parser
        return this
    }

    fun exec(client: OkHttpClient = OkHttpClient()): R {
        return try {
            body = client.newCall(request).execute().body
            _parser.invoke(bodyString)
        } catch (e: Exception) {
            Log.e("http", "Error: $e", e)
            _onerror.invoke(e)
        }
    }
}

inline fun <R> buildOkHttp(builder: Request.Builder.() -> Unit): OkHttpWarp<R> {
    val request = Request.Builder()
        .header(
            "User-Agent",
            "Mozilla/5.0 (Linux; Android 7.1.1; 1605-A01 Build/NMF26F; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/79.0.3945.93 Mobile Safari/537.36"
        )
        .apply(builder)
        .build()
    return OkHttpWarp(request)
}