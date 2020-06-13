package com.linkensky.ornet.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import com.linkensky.ornet.R
import kotlinx.android.synthetic.main.activity_webview.*

class WebviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        setSupportActionBar(toolbarWebView)
        supportActionBar?.title = "SIKM Prov. Gorontalo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun init() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setAppCachePath(cacheDir.path)
        settings.setSupportZoom(true)
        settings.blockNetworkImage = false
        settings.loadsImagesAutomatically = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.loadWithOverviewMode = true
        settings.allowContentAccess = true
        settings.setGeolocationEnabled(true)
        settings.allowUniversalAccessFromFileURLs = true
        settings.allowFileAccess = true
        webView.fitsSystemWindows = true
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.loadUrl(intent.getStringExtra("url"))
        webView.webChromeClient = WebChromeClient()
    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(Activity.RESULT_OK, Intent())
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent())
        super.onBackPressed()
    }
}
