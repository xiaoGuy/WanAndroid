package com.xg.wanandroid.splash

import android.os.Bundle
import android.view.View
import com.xg.wanandroid.MainActivity
import com.xg.wanandroid.common.ui.BaseActivity
import com.xg.wanandroid.core.extension.postDelay
import wanandroid.xg.com.wanandroid.R

class SplashActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_splash
    override fun showToolBar() = false

    override fun initViews(view: View, savedInstanceState: Bundle?) {
        postDelay(2000) {
            MainActivity.start(this)
            finish()
        }
    }

}