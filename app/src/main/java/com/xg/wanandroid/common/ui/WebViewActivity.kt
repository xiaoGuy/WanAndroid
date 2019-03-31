package com.xg.wanandroid.common.ui

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_web_view.*
import wanandroid.xg.com.wanandroid.R

class WebViewActivity : BaseActivity() {

    companion object {
        private const val TITLE = "title"
        private const val URL = "url"

        fun start(context: Context, title: CharSequence, url: String) {
            val intent = Intent(context, WebViewActivity::class.java).apply {
                putExtra(TITLE, title)
                putExtra(URL, url)
            }
            context.startActivity(intent)
        }
    }

    override fun getLayoutId() = R.layout.activity_web_view

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews(view: View, savedInstanceState: Bundle?) {
        updateTitle(intent.getCharSequenceExtra(TITLE))

        webView.settings.javaScriptEnabled = true
        webView.loadUrl(intent.getStringExtra(URL))
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showLoadingView()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                showContentView()
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return false
            }
        }
    }

    override fun onBackPressedSupport() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }

}