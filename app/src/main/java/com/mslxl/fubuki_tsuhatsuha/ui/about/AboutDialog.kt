package com.mslxl.fubuki_tsuhatsuha.ui.about

import android.app.AlertDialog
import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mslxl.fubuki_tsuhatsuha.R

class AboutDialog {
    fun show(context: Context) {
        val builder = AlertDialog.Builder(context)
        val view = WebView(context).apply {
            webViewClient = WebViewClient()
            loadUrl("file:///android_asset/about.html")
        }.let {
            builder.setView(it)
        }
        builder.setPositiveButton(R.string.confirm, null)
        builder.show()

    }
}